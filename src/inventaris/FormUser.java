package inventaris;

import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.swing.JComboBox;
import javax.swing.JTextField;

/** CRUD tabel user (perbaikan Pertemuan 12). Hanya tampil untuk role admin. */
public class FormUser extends FormDasar {

    private final JTextField txtNama = new JTextField(25);
    private final JTextField txtUsername = new JTextField(15);
    private final JComboBox<String> cmbRole = new JComboBox<>(new String[]{"petugas", "admin"});

    public FormUser() {
        super("Data Pengguna", new String[]{"ID", "Nama Pengguna", "Username", "Role"});
        tambahInput("Nama Pengguna", txtNama);
        tambahInput("Username", txtUsername);
        tambahInput("Role", cmbRole);
    }

    @Override
    protected void muatData() throws Exception {
        Util.isiTabel(model, "SELECT id_user, nama_user, username, role FROM `user` ORDER BY id_user");
    }

    private void isiParameter(PreparedStatement ps) throws Exception {
        String nama = txtNama.getText().trim();
        String username = txtUsername.getText().trim().toLowerCase();
        if (nama.isEmpty() || username.isEmpty()) {
            throw new IllegalArgumentException("Nama pengguna dan username wajib diisi.");
        }
        if (!username.matches("[a-z0-9_.]{3,50}")) {
            throw new IllegalArgumentException("Username minimal 3 karakter, hanya huruf kecil, angka, titik, atau garis bawah.");
        }
        ps.setString(1, nama);
        ps.setString(2, username);
        ps.setString(3, (String) cmbRole.getSelectedItem());
    }

    @Override
    protected void simpan() throws Exception {
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "INSERT INTO `user` (nama_user, username, role) VALUES (?, ?, ?)")) {
            isiParameter(ps);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalStateException("Username sudah dipakai, gunakan username lain.");
        }
        Util.info(this, "Pengguna berhasil disimpan.");
    }

    @Override
    protected void ubah() throws Exception {
        wajibPilih();
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "UPDATE `user` SET nama_user = ?, username = ?, role = ? WHERE id_user = ?")) {
            isiParameter(ps);
            ps.setInt(4, idTerpilih());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalStateException("Username sudah dipakai, gunakan username lain.");
        }
        Util.info(this, "Pengguna berhasil diubah.");
    }

    @Override
    protected void hapus() throws Exception {
        wajibPilih();
        if (idTerpilih() == Sesi.getIdUser()) {
            throw new IllegalStateException("Pengguna yang sedang dipakai tidak bisa dihapus.");
        }
        if (!Util.konfirmasi(this, "Hapus pengguna ini?")) {
            return;
        }
        try (PreparedStatement ps = Koneksi.getKoneksi().prepareStatement(
                "DELETE FROM `user` WHERE id_user = ?")) {
            ps.setInt(1, idTerpilih());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalStateException("Pengguna sudah tercatat pada transaksi, tidak bisa dihapus.");
        }
        Util.info(this, "Pengguna berhasil dihapus.");
    }

    @Override
    protected void isiDariTabel(int baris) {
        txtNama.setText(String.valueOf(model.getValueAt(baris, 1)));
        txtUsername.setText(String.valueOf(model.getValueAt(baris, 2)));
        cmbRole.setSelectedItem(String.valueOf(model.getValueAt(baris, 3)));
    }

    @Override
    protected void bersihkan() {
        txtNama.setText("");
        txtUsername.setText("");
        cmbRole.setSelectedIndex(0);
        tabel.clearSelection();
    }
}
