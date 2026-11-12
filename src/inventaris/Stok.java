package inventaris;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Menjaga konsistensi stok barang ketika transaksi masuk/keluar
 * ditambah, diubah, atau dihapus. Semua perubahan dijalankan dalam satu
 * transaksi database (commit/rollback) agar stok tidak pernah setengah jadi.
 */
public final class Stok {

    private Stok() {
    }

    @FunctionalInterface
    public interface Pekerjaan {
        void jalankan(Connection c) throws Exception;
    }

    /** Menjalankan beberapa perintah SQL sebagai satu transaksi. */
    public static void transaksi(Pekerjaan pekerjaan) throws Exception {
        Connection c = Koneksi.getKoneksi();
        c.setAutoCommit(false);
        try {
            pekerjaan.jalankan(c);
            c.commit();
        } catch (Exception e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(true);
        }
    }

    /** Menambah (selisih positif) atau mengurangi (selisih negatif) stok barang. */
    public static void ubah(Connection c, int idBarang, int selisih) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE barang SET stok = stok + ? WHERE id_barang = ? AND stok + ? >= 0")) {
            ps.setInt(1, selisih);
            ps.setInt(2, idBarang);
            ps.setInt(3, selisih);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Stok barang tidak mencukupi untuk transaksi ini.");
            }
        }
    }

    /** Mengambil {id_barang, jumlah} lama dari sebuah transaksi (dikunci FOR UPDATE). */
    public static int[] dataLama(Connection c, String tabel, String kolomId, int id) throws SQLException {
        String sql = "SELECT id_barang, jumlah FROM " + tabel + " WHERE " + kolomId + " = ? FOR UPDATE";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Data transaksi tidak ditemukan.");
                }
                return new int[]{rs.getInt(1), rs.getInt(2)};
            }
        }
    }
}
