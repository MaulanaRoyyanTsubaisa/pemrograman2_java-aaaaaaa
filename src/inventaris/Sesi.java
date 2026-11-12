package inventaris;

/**
 * Menyimpan pengguna yang sedang memakai aplikasi (hasil pilihan saat aplikasi dibuka).
 * Dipakai untuk mencatat siapa yang melakukan transaksi dan perubahan harga
 * (perbaikan desain Pertemuan 12).
 */
public final class Sesi {

    private static int idUser;
    private static String namaUser = "-";
    private static String role = "petugas";

    private Sesi() {
    }

    public static void masuk(int id, String nama, String peran) {
        idUser = id;
        namaUser = nama;
        role = peran;
    }

    public static int getIdUser() {
        if (idUser <= 0) {
            throw new IllegalStateException("Belum ada pengguna yang masuk.");
        }
        return idUser;
    }

    public static String getNamaUser() {
        return namaUser;
    }

    public static boolean isAdmin() {
        return "admin".equals(role);
    }

    public static String getRole() {
        return role;
    }
}
