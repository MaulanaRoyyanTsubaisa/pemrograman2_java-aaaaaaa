package inventaris;

import java.sql.PreparedStatement;
import javax.swing.JTextField;

/** CRUD tabel kategori. */
public class FormKategori extends FormDasar {

    private final JTextField txtNama = new JTextField(25);

    public FormKategori() {
        super("Data Kategori", new String[]{"ID", "Nama Kategori"});
        tambahInput("Nama Kategori", txtNama);
    }

    @Override
    protected void muatData() throws Exception {
        Util.isiTabel(model, "SELECT id_kategori, nama_kategori FROM kategori ORDER BY id_kategori");
    }

    private String nama() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            throw new IllegalArgumentException("Nama kategori wajib diisi.");
        }
        return nama;
    }

    @Override
    protected void simpan() throws Exception {
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "INSERT INTO kategori (nama_kategori) VALUES (?)")) {
            ps.setString(1, nama());
            ps.executeUpdate();
        }
        Util.info(this, "Kategori berhasil disimpan.");
    }

    @Override
    protected void ubah() throws Exception {
        wajibPilih();
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "UPDATE kategori SET nama_kategori = ? WHERE id_kategori = ?")) {
            ps.setString(1, nama());
            ps.setInt(2, idTerpilih());
            ps.executeUpdate();
        }
        Util.info(this, "Kategori berhasil diubah.");
    }

    @Override
    protected void hapus() throws Exception {
        wajibPilih();
        if (!Util.konfirmasi(this, "Hapus kategori ini?")) {
            return;
        }
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "DELETE FROM kategori WHERE id_kategori = ?")) {
            ps.setInt(1, idTerpilih());
            ps.executeUpdate();
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new IllegalStateException("Kategori masih dipakai oleh data barang, tidak bisa dihapus.");
        }
        Util.info(this, "Kategori berhasil dihapus.");
    }

    @Override
    protected void isiDariTabel(int baris) {
        txtNama.setText(String.valueOf(model.getValueAt(baris, 1)));
    }

    @Override
    protected void bersihkan() {
        txtNama.setText("");
        tabel.clearSelection();
    }
}
