package inventaris;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/** CRUD tabel barang_keluar. Setiap transaksi keluar mengurangi stok barang. */
public class FormBarangKeluar extends FormDasar {

    private final JComboBox<Util.Item> cmbBarang = new JComboBox<>();
    private final JTextField txtTanggal = new JTextField(10);
    private final JSpinner spnJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 1_000_000, 1));

    public FormBarangKeluar() {
        super("Transaksi Barang Keluar",
                new String[]{"ID", "Tanggal", "ID Barang", "Barang", "Jumlah", "Petugas"});
        tambahInput("Tanggal (yyyy-mm-dd)", txtTanggal);
        tambahInput("Barang", cmbBarang);
        tambahInput("Jumlah", spnJumlah);
        tabel.removeColumn(tabel.getColumnModel().getColumn(2));
        txtTanggal.setText(LocalDate.now().toString());
    }

    @Override
    protected void muatPilihan() throws Exception {
        Util.isiCombo(cmbBarang,
                "SELECT id_barang, CONCAT(nama_barang, ' (stok ', stok, ')') FROM barang ORDER BY nama_barang");
    }

    @Override
    protected void muatData() throws Exception {
        Util.isiTabel(model,
                "SELECT k.id_keluar, k.tanggal_keluar, k.id_barang, b.nama_barang, k.jumlah, u.nama_user "
                + "FROM barang_keluar k JOIN barang b ON b.id_barang = k.id_barang "
                + "JOIN `user` u ON u.id_user = k.id_user "
                + "ORDER BY k.tanggal_keluar DESC, k.id_keluar DESC");
    }

    private Date tanggal() {
        try {
            return Date.valueOf(LocalDate.parse(txtTanggal.getText().trim()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format tanggal harus yyyy-mm-dd, contoh 2026-11-12.");
        }
    }

    private int idBarang() {
        Util.Item item = (Util.Item) cmbBarang.getSelectedItem();
        if (item == null) {
            throw new IllegalArgumentException("Pilih barang terlebih dahulu.");
        }
        return item.id;
    }

    @Override
    protected void simpan() throws Exception {
        int idBarang = idBarang();
        int jumlah = (Integer) spnJumlah.getValue();
        Date tgl = tanggal();
        Stok.transaksi(c -> {
            Stok.ubah(c, idBarang, -jumlah);   // gagal bila stok tidak cukup
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO barang_keluar (id_barang, id_user, tanggal_keluar, jumlah) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, idBarang);
                ps.setInt(2, Sesi.getIdUser());   // petugas yang mencatat
                ps.setDate(3, tgl);
                ps.setInt(4, jumlah);
                ps.executeUpdate();
            }
        });
        Util.info(this, "Barang keluar tersimpan, stok berkurang " + jumlah + ".");
        muatPilihan();
    }

    @Override
    protected void ubah() throws Exception {
        wajibPilih();
        int id = idTerpilih();
        int idBarang = idBarang();
        int jumlah = (Integer) spnJumlah.getValue();
        Date tgl = tanggal();
        Stok.transaksi(c -> {
            int[] lama = Stok.dataLama(c, "barang_keluar", "id_keluar", id);
            Stok.ubah(c, lama[0], +lama[1]);   // kembalikan stok transaksi lama
            Stok.ubah(c, idBarang, -jumlah);   // kurangi sesuai transaksi baru
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE barang_keluar SET id_barang = ?, tanggal_keluar = ?, jumlah = ? WHERE id_keluar = ?")) {
                ps.setInt(1, idBarang);
                ps.setDate(2, tgl);
                ps.setInt(3, jumlah);
                ps.setInt(4, id);
                ps.executeUpdate();
            }
        });
        Util.info(this, "Transaksi barang keluar berhasil diubah dan stok disesuaikan.");
        muatPilihan();
    }

    @Override
    protected void hapus() throws Exception {
        wajibPilih();
        if (!Util.konfirmasi(this, "Hapus transaksi ini? Stok barang akan dikembalikan.")) {
            return;
        }
        int id = idTerpilih();
        Stok.transaksi(c -> {
            int[] lama = Stok.dataLama(c, "barang_keluar", "id_keluar", id);
            Stok.ubah(c, lama[0], +lama[1]);
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM barang_keluar WHERE id_keluar = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        });
        Util.info(this, "Transaksi barang keluar berhasil dihapus.");
        muatPilihan();
    }

    @Override
    protected void isiDariTabel(int baris) {
        txtTanggal.setText(String.valueOf(model.getValueAt(baris, 1)));
        Util.pilihCombo(cmbBarang, ((Number) model.getValueAt(baris, 2)).intValue());
        spnJumlah.setValue(((Number) model.getValueAt(baris, 4)).intValue());
    }

    @Override
    protected void bersihkan() {
        txtTanggal.setText(LocalDate.now().toString());
        spnJumlah.setValue(1);
        tabel.clearSelection();
    }
}
