package inventaris;

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/** Fungsi bantu yang dipakai bersama oleh semua form CRUD. */
public final class Util {

    private Util() {
    }

    /** Item combobox yang menyimpan id (untuk foreign key) dan teks tampilan. */
    public static class Item {
        public final int id;
        public final String label;

        public Item(int id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /** Menjalankan query SELECT lalu mengisi JTable model (Read). */
    public static void isiTabel(DefaultTableModel model, String sql) throws SQLException {
        model.setRowCount(0);
        Connection c = Koneksi.getKoneksi();
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                Vector<Object> baris = new Vector<>();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    baris.add(rs.getObject(i));
                }
                model.addRow(baris);
            }
        }
    }

    /** Mengisi combobox dari query "SELECT id, label FROM ...". */
    public static void isiCombo(JComboBox<Item> combo, String sql) throws SQLException {
        combo.removeAllItems();
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                combo.addItem(new Item(rs.getInt(1), rs.getString(2)));
            }
        }
    }

    public static void pilihCombo(JComboBox<Item> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Mode pengujian otomatis: dialog tidak ditampilkan, pesan terakhir disimpan
     * di {@link #pesanTerakhir} dan konfirmasi dianggap "Ya".
     */
    public static boolean modeOtomatis = false;
    public static String pesanTerakhir = "";

    public static void info(Component parent, String pesan) {
        pesanTerakhir = pesan;
        if (!modeOtomatis) {
            JOptionPane.showMessageDialog(parent, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void error(Component parent, Exception e) {
        pesanTerakhir = e.getMessage();
        if (!modeOtomatis) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "Terjadi Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static boolean konfirmasi(Component parent, String pesan) {
        return modeOtomatis || JOptionPane.showConfirmDialog(parent, pesan, "Konfirmasi",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
