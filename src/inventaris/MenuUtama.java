package inventaris;

import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
        tab.addTab("Cetak Laporan", new PanelLaporan());

        // setiap kali tab dibuka, data dan pilihan combobox dimuat ulang dari database
        tab.addChangeListener(e -> segarkan(tab.getSelectedComponent()));
        segarkan(tab.getSelectedComponent());

        add(tab);
        setSize(900, 620);
        setLocationRelativeTo(null);
    }

    private static void segarkan(Component c) {
        if (c instanceof FormDasar) {
            ((FormDasar) c).segarkan();
        }
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
