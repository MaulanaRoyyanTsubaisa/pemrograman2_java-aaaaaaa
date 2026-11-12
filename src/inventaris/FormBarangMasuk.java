package inventaris;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/** CRUD tabel barang_masuk. Setiap transaksi masuk menambah stok barang. */
public class FormBarangMasuk extends FormDasar {

    private final JComboBox<Util.Item> cmbBarang = new JComboBox<>();
    private final JComboBox<Util.Item> cmbSupplier = new JComboBox<>();
    private final JTextField txtTanggal = new JTextField(10);
    private final JSpinner spnJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 1_000_000, 1));

    public FormBarangMasuk() {
        super("Transaksi Barang Masuk",
                new String[]{"ID", "Tanggal", "ID Barang", "Barang", "ID Supplier", "Supplier", "Jumlah"});
        tambahInput("Tanggal (yyyy-mm-dd)", txtTanggal);
        tambahInput("Barang", cmbBarang);
        tambahInput("Supplier", cmbSupplier);
        tambahInput("Jumlah", spnJumlah);
        tabel.removeColumn(tabel.getColumnModel().getColumn(4));
        tabel.removeColumn(tabel.getColumnModel().getColumn(2));
        txtTanggal.setText(LocalDate.now().toString());
    }

    @Override
    protected void muatPilihan() throws Exception {
        Util.isiCombo(cmbBarang, "SELECT id_barang, nama_barang FROM barang ORDER BY nama_barang");
        Util.isiCombo(cmbSupplier, "SELECT id_supplier, nama_supplier FROM supplier ORDER BY nama_supplier");
    }

    @Override
    protected void muatData() throws Exception {
        Util.isiTabel(model,
                "SELECT m.id_masuk, m.tanggal_masuk, m.id_barang, b.nama_barang, "
                + "m.id_supplier, s.nama_supplier, m.jumlah "
                + "FROM barang_masuk m "
                + "JOIN barang b ON b.id_barang = m.id_barang "
                + "JOIN supplier s ON s.id_supplier = m.id_supplier "
                + "ORDER BY m.tanggal_masuk DESC, m.id_masuk DESC");
    }

    private Date tanggal() {
        try {
            return Date.valueOf(LocalDate.parse(txtTanggal.getText().trim()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format tanggal harus yyyy-mm-dd, contoh 2026-11-12.");
        }
    }

    private Util.Item pilihan(JComboBox<Util.Item> combo, String nama) {
        Util.Item item = (Util.Item) combo.getSelectedItem();
        if (item == null) {
            throw new IllegalArgumentException("Pilih " + nama + " terlebih dahulu.");
        }
        return item;
    }

    @Override
    protected void simpan() throws Exception {
        int idBarang = pilihan(cmbBarang, "barang").id;
        int idSupplier = pilihan(cmbSupplier, "supplier").id;
        int jumlah = (Integer) spnJumlah.getValue();
        Date tgl = tanggal();
        Stok.transaksi(c -> {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO barang_masuk (id_barang, id_supplier, tanggal_masuk, jumlah) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, idBarang);
                ps.setInt(2, idSupplier);
                ps.setDate(3, tgl);
                ps.setInt(4, jumlah);
                ps.executeUpdate();
            }
            Stok.ubah(c, idBarang, +jumlah);
        });
        Util.info(this, "Barang masuk tersimpan, stok bertambah " + jumlah + ".");
    }

    @Override
    protected void ubah() throws Exception {
        wajibPilih();
        int id = idTerpilih();
        int idBarang = pilihan(cmbBarang, "barang").id;
        int idSupplier = pilihan(cmbSupplier, "supplier").id;
        int jumlah = (Integer) spnJumlah.getValue();
        Date tgl = tanggal();
        Stok.transaksi(c -> {
            int[] lama = Stok.dataLama(c, "barang_masuk", "id_masuk", id);
            if (lama[0] == idBarang) {
                Stok.ubah(c, idBarang, jumlah - lama[1]);  // barang sama: cukup selisihnya
            } else {
                Stok.ubah(c, lama[0], -lama[1]);   // batalkan efek transaksi lama
                Stok.ubah(c, idBarang, +jumlah);   // terapkan transaksi baru
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE barang_masuk SET id_barang = ?, id_supplier = ?, tanggal_masuk = ?, jumlah = ? "
                    + "WHERE id_masuk = ?")) {
                ps.setInt(1, idBarang);
                ps.setInt(2, idSupplier);
                ps.setDate(3, tgl);
                ps.setInt(4, jumlah);
                ps.setInt(5, id);
                ps.executeUpdate();
            }
        });
        Util.info(this, "Transaksi barang masuk berhasil diubah dan stok disesuaikan.");
    }

    @Override
    protected void hapus() throws Exception {
        wajibPilih();
        if (!Util.konfirmasi(this, "Hapus transaksi ini? Stok barang akan dikurangi kembali.")) {
            return;
        }
        int id = idTerpilih();
        Stok.transaksi(c -> {
            int[] lama = Stok.dataLama(c, "barang_masuk", "id_masuk", id);
            Stok.ubah(c, lama[0], -lama[1]);
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM barang_masuk WHERE id_masuk = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        });
        Util.info(this, "Transaksi barang masuk berhasil dihapus.");
    }

    @Override
    protected void isiDariTabel(int baris) {
        txtTanggal.setText(String.valueOf(model.getValueAt(baris, 1)));
        Util.pilihCombo(cmbBarang, ((Number) model.getValueAt(baris, 2)).intValue());
        Util.pilihCombo(cmbSupplier, ((Number) model.getValueAt(baris, 4)).intValue());
        spnJumlah.setValue(((Number) model.getValueAt(baris, 6)).intValue());
    }

    @Override
    protected void bersihkan() {
        txtTanggal.setText(LocalDate.now().toString());
        spnJumlah.setValue(1);
        tabel.clearSelection();
    }
}
