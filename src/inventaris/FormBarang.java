package inventaris;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/** CRUD tabel barang (relasi ke kategori). */
public class FormBarang extends FormDasar {

    private final JTextField txtNama = new JTextField(25);
    private final JComboBox<Util.Item> cmbKategori = new JComboBox<>();
    private final JSpinner spnStok = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 1));
    private final JTextField txtHarga = new JTextField(12);

    public FormBarang() {
        super("Data Barang", new String[]{"ID", "Nama Barang", "ID Kategori", "Kategori", "Stok", "Harga"});
        tambahInput("Nama Barang", txtNama);
        tambahInput("Kategori", cmbKategori);
        tambahInput("Stok Awal", spnStok);
        tambahInput("Harga (Rp)", txtHarga);
        // kolom ID Kategori disembunyikan, hanya dipakai saat memilih baris
        tabel.removeColumn(tabel.getColumnModel().getColumn(2));
    }

    @Override
    protected void muatPilihan() throws Exception {
        Util.isiCombo(cmbKategori, "SELECT id_kategori, nama_kategori FROM kategori ORDER BY nama_kategori");
    }

    @Override
    protected void muatData() throws Exception {
        Util.isiTabel(model,
                "SELECT b.id_barang, b.nama_barang, b.id_kategori, k.nama_kategori, b.stok, b.harga "
                + "FROM barang b JOIN kategori k ON k.id_kategori = b.id_kategori ORDER BY b.id_barang");
    }

    private void isiParameter(PreparedStatement ps) throws Exception {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            throw new IllegalArgumentException("Nama barang wajib diisi.");
        }
        Util.Item kategori = (Util.Item) cmbKategori.getSelectedItem();
        if (kategori == null) {
            throw new IllegalArgumentException("Isi data kategori terlebih dahulu.");
        }
        BigDecimal harga;
        try {
            harga = new BigDecimal(txtHarga.getText().trim().replace(".", "").replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Harga harus berupa angka.");
        }
        if (harga.signum() < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif.");
        }
        ps.setString(1, nama);
        ps.setInt(2, kategori.id);
        ps.setInt(3, (Integer) spnStok.getValue());
        ps.setBigDecimal(4, harga);
    }

    @Override
    protected void simpan() throws Exception {
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "INSERT INTO barang (nama_barang, id_kategori, stok, harga) VALUES (?, ?, ?, ?)")) {
            isiParameter(ps);
            ps.executeUpdate();
        }
        Util.info(this, "Barang berhasil disimpan.");
    }

    @Override
    protected void ubah() throws Exception {
        wajibPilih();
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "UPDATE barang SET nama_barang = ?, id_kategori = ?, stok = ?, harga = ? WHERE id_barang = ?")) {
            isiParameter(ps);
            ps.setInt(5, idTerpilih());
            ps.executeUpdate();
        }
        Util.info(this, "Barang berhasil diubah.");
    }

    @Override
    protected void hapus() throws Exception {
        wajibPilih();
        if (!Util.konfirmasi(this, "Hapus barang ini?")) {
            return;
        }
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "DELETE FROM barang WHERE id_barang = ?")) {
            ps.setInt(1, idTerpilih());
            ps.executeUpdate();
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            throw new IllegalStateException("Barang sudah memiliki riwayat transaksi, tidak bisa dihapus.");
        }
        Util.info(this, "Barang berhasil dihapus.");
    }

    @Override
    protected void isiDariTabel(int baris) {
        txtNama.setText(String.valueOf(model.getValueAt(baris, 1)));
        Util.pilihCombo(cmbKategori, ((Number) model.getValueAt(baris, 2)).intValue());
        spnStok.setValue(((Number) model.getValueAt(baris, 4)).intValue());
        txtHarga.setText(((BigDecimal) model.getValueAt(baris, 5)).stripTrailingZeros().toPlainString().replace('.', ','));
    }

    @Override
    protected void bersihkan() {
        txtNama.setText("");
        if (cmbKategori.getItemCount() > 0) {
            cmbKategori.setSelectedIndex(0);
        }
        spnStok.setValue(0);
        txtHarga.setText("");
        tabel.clearSelection();
    }
}
