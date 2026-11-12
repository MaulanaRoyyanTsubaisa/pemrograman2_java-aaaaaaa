package inventaris;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/** Jendela utama Sistem Inventaris Toko. */
public class MenuUtama extends JFrame {

    public MenuUtama() {
        super("Sistem Inventaris Toko - " + Koneksi.NAMA_DATABASE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Kategori", new FormKategori());
        tab.addTab("Supplier", new FormSupplier());
        tab.addTab("Barang", new FormBarang());
        tab.addTab("Barang Masuk", new FormBarangMasuk());
        tab.addTab("Barang Keluar", new FormBarangKeluar());
        if (Sesi.isAdmin()) {
            tab.addTab("Pengguna", new FormUser());   // hanya admin yang boleh mengelola pengguna
        }
        tab.addTab("Cetak Laporan", new PanelLaporan());

        // setiap kali tab dibuka, data dan pilihan combobox dimuat ulang dari database
        tab.addChangeListener(e -> segarkan(tab.getSelectedComponent()));
        segarkan(tab.getSelectedComponent());

        JLabel status = new JLabel("Pengguna: " + Sesi.getNamaUser() + " (" + Sesi.getRole() + ")"
                + "   |   Database: " + Koneksi.NAMA_DATABASE);
        status.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        JPanel isi = new JPanel(new BorderLayout());
        isi.add(tab, BorderLayout.CENTER);
        isi.add(status, BorderLayout.SOUTH);
        setContentPane(isi);
        setSize(920, 640);
        setLocationRelativeTo(null);
    }

    private static void segarkan(Component c) {
        if (c instanceof FormDasar) {
            ((FormDasar) c).segarkan();
        }
    }

    /** Dialog memilih pengguna yang akan memakai aplikasi. */
    private static boolean pilihPengguna() throws SQLException {
        JComboBox<Util.Item> cmbUser = new JComboBox<>();
        Util.isiCombo(cmbUser, "SELECT id_user, CONCAT(nama_user, ' (', username, ' - ', role, ')') "
                + "FROM `user` ORDER BY role, nama_user");
        if (cmbUser.getItemCount() == 0) {
            JOptionPane.showMessageDialog(null, "Tabel user masih kosong. Impor ulang database terlebih dahulu.");
            return false;
        }
        int pilihan = JOptionPane.showConfirmDialog(null, new Object[]{"Masuk sebagai:", cmbUser},
                "Sistem Inventaris Toko", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (pilihan != JOptionPane.OK_OPTION) {
            return false;
        }
        int id = ((Util.Item) cmbUser.getSelectedItem()).id;
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "SELECT nama_user, role FROM `user` WHERE id_user = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                Sesi.masuk(id, rs.getString(1), rs.getString(2));
            }
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // pakai tampilan bawaan
        }
        SwingUtilities.invokeLater(() -> {
            try {
                Koneksi.getKoneksi();
                if (!pilihPengguna()) {
                    System.exit(0);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                        "Gagal terhubung ke MySQL.\nPastikan MySQL (Laragon/XAMPP) sudah berjalan dan database "
                        + Koneksi.NAMA_DATABASE + " sudah diimpor.\n\n" + e.getMessage(),
                        "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            new MenuUtama().setVisible(true);
        });
    }
}
