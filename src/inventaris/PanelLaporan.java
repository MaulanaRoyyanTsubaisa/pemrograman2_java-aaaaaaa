package inventaris;

import inventaris.laporan.JenisLaporan;
import inventaris.laporan.LaporanGenerator;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

/** Menu Cetak Laporan: pilih tabel, lalu laporan HTML dibuka di browser. */
public class PanelLaporan extends JPanel {

    private final ButtonGroup grup = new ButtonGroup();
    private final JTextArea log = new JTextArea(6, 40);
    private JenisLaporan terpilih = JenisLaporan.BARANG;

    public PanelLaporan() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel judul = new JLabel("Cetak Laporan dari Database MySQL");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 16f));

        JPanel pilihan = new JPanel();
        pilihan.setLayout(new BoxLayout(pilihan, BoxLayout.Y_AXIS));
        pilihan.setBorder(BorderFactory.createTitledBorder("Pilih laporan"));
        for (JenisLaporan j : JenisLaporan.values()) {
            JRadioButton rb = new JRadioButton(j.toString(), j == terpilih);
            rb.addActionListener(e -> terpilih = j);
            grup.add(rb);
            pilihan.add(rb);
        }

        JButton btnCetak = new JButton("Cetak Laporan");
        JButton btnFolder = new JButton("Buka Folder Laporan");
        btnCetak.addActionListener(e -> cetak());
        btnFolder.addActionListener(e -> bukaFolder());
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tombol.add(btnCetak);
        tombol.add(btnFolder);

        log.setEditable(false);
        log.setText("Laporan dibuat dalam format HTML lalu dibuka di browser.\n"
                + "Klik tombol \"Cetak Laporan\" di halaman browser untuk mencetak atau Simpan sebagai PDF.\n");

        JPanel tengah = new JPanel(new BorderLayout(8, 8));
        tengah.add(pilihan, BorderLayout.NORTH);
        tengah.add(tombol, BorderLayout.CENTER);

        add(judul, BorderLayout.NORTH);
        add(tengah, BorderLayout.CENTER);
        add(log, BorderLayout.SOUTH);
    }

    private void cetak() {
        try {
            Path file = new LaporanGenerator().buat(terpilih);
            LaporanGenerator.bukaDiBrowser(file);
            log.append("Dibuat: " + file + "\n");
        } catch (Exception e) {
            Util.error(this, e);
        }
    }

    private void bukaFolder() {
        try {
            Path folder = Paths.get("laporan").toAbsolutePath();
            Files.createDirectories(folder);
            Desktop.getDesktop().open(folder.toFile());
        } catch (Exception e) {
            Util.error(this, e);
        }
    }
}
