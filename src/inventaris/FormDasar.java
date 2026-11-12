package inventaris;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Kerangka tampilan form CRUD: form input di atas, tombol aksi, dan tabel data di bawah.
 * Setiap tabel database punya subclass sendiri (FormKategori, FormBarang, dst).
 */
public abstract class FormDasar extends JPanel {

    protected final DefaultTableModel model;
    protected final JTable tabel;
    private final JPanel panelInput = new JPanel(new GridBagLayout());
    private int barisInput = 0;

    protected FormDasar(String judul, String[] kolom) {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabel = new JTable(model);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setRowHeight(24);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabel.getSelectedRow() >= 0) {
                isiDariTabel(tabel.getSelectedRow());
            }
        });

        panelInput.setBorder(BorderFactory.createTitledBorder(judul));

        JButton btnSimpan = new JButton("Simpan");
        JButton btnUbah = new JButton("Ubah");
        JButton btnHapus = new JButton("Hapus");
        JButton btnBersih = new JButton("Bersihkan");
        btnSimpan.addActionListener(e -> jalankan(this::simpan));
        btnUbah.addActionListener(e -> jalankan(this::ubah));
        btnHapus.addActionListener(e -> jalankan(this::hapus));
        btnBersih.addActionListener(e -> bersihkan());

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTombol.add(btnSimpan);
        panelTombol.add(btnUbah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnBersih);

        JPanel atas = new JPanel(new BorderLayout());
        atas.add(panelInput, BorderLayout.CENTER);
        atas.add(panelTombol, BorderLayout.SOUTH);

        add(atas, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);
    }

    /** Menambah satu baris label + komponen input pada form. */
    protected void tambahInput(String label, JComponent komponen) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.gridy = barisInput++;
        g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;
        panelInput.add(new JLabel(label), g);
        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        panelInput.add(komponen, g);
    }

    /** Baris yang dipilih pada tabel, atau -1 bila belum ada. */
    protected int idTerpilih() {
        int baris = tabel.getSelectedRow();
        return baris < 0 ? -1 : ((Number) model.getValueAt(baris, 0)).intValue();
    }

    protected void wajibPilih() {
        if (idTerpilih() < 0) {
            throw new IllegalStateException("Pilih data pada tabel terlebih dahulu.");
        }
    }

    private void jalankan(Aksi aksi) {
        try {
            aksi.run();
            muatData();
            bersihkan();
        } catch (Exception e) {
            Util.error(this, e);
        }
    }

    @FunctionalInterface
    private interface Aksi {
        void run() throws Exception;
    }

    /** Dipanggil ketika tab dibuka, agar combobox dan tabel selalu berisi data terbaru. */
    public void segarkan() {
        try {
            muatPilihan();
            muatData();
        } catch (Exception e) {
            Util.error(this, e);
        }
    }

    protected void muatPilihan() throws Exception {
        // diisi oleh form yang punya combobox foreign key
    }

    protected abstract void muatData() throws Exception;      // Read

    protected abstract void simpan() throws Exception;        // Create

    protected abstract void ubah() throws Exception;          // Update

    protected abstract void hapus() throws Exception;         // Delete

    protected abstract void isiDariTabel(int baris);

    protected abstract void bersihkan();
}
