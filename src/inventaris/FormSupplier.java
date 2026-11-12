package inventaris;

import java.sql.PreparedStatement;
import javax.swing.JTextField;

/** CRUD tabel supplier. */
public class FormSupplier extends FormDasar {

    private final JTextField txtNama = new JTextField(25);
    private final JTextField txtAlamat = new JTextField(25);
    private final JTextField txtTelp = new JTextField(15);

    public FormSupplier() {
        super("Data Supplier", new String[]{"ID", "Nama Supplier", "Alamat", "No. Telepon"});
        tambahInput("Nama Supplier", txtNama);
        tambahInput("Alamat", txtAlamat);
        tambahInput("No. Telepon", txtTelp);
    }

    @Override
    protected void muatData() throws Exception {
        Util.isiTabel(model, "SELECT id_supplier, nama_supplier, alamat, no_telp FROM supplier ORDER BY id_supplier");
    }

    private void isiParameter(PreparedStatement ps) throws Exception {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            throw new IllegalArgumentException("Nama supplier wajib diisi.");
        }
        String telp = txtTelp.getText().trim();
        if (!telp.matches("[0-9+\\- ]*")) {
            throw new IllegalArgumentException("Nomor telepon hanya boleh berisi angka.");
        }
        ps.setString(1, nama);
        ps.setString(2, txtAlamat.getText().trim());
        ps.setString(3, telp);
    }

    @Override
    protected void simpan() throws Exception {
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "INSERT INTO supplier (nama_supplier, alamat, no_telp) VALUES (?, ?, ?)")) {
            isiParameter(ps);
            ps.executeUpdate();
        }
        Util.info(this, "Supplier berhasil disimpan.");
    }

    @Override
    protected void ubah() throws Exception {
        wajibPilih();
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "UPDATE supplier SET nama_supplier = ?, alamat = ?, no_telp = ? WHERE id_supplier = ?")) {
            isiParameter(ps);
            ps.setInt(4, idTerpilih());
            ps.executeUpdate();
        }
        Util.info(this, "Supplier berhasil diubah.");
    }

    @Override
    protected void hapus() throws Exception {
        wajibPilih();
        if (!Util.konfirmasi(this, "Hapus supplier ini?")) {
            return;
        }
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "DELETE FROM supplier WHERE id_supplier = ?")) {
            ps.setInt(1, idTerpilih());
            ps.executeUpdate();
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new IllegalStateException("Supplier masih memiliki transaksi barang masuk, tidak bisa dihapus.");
        }
        Util.info(this, "Supplier berhasil dihapus.");
    }

    @Override
    protected void isiDariTabel(int baris) {
        txtNama.setText(teks(baris, 1));
        txtAlamat.setText(teks(baris, 2));
        txtTelp.setText(teks(baris, 3));
    }

    private String teks(int baris, int kolom) {
        Object v = model.getValueAt(baris, kolom);
        return v == null ? "" : v.toString();
    }

    @Override
    protected void bersihkan() {
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelp.setText("");
        tabel.clearSelection();
    }
}
