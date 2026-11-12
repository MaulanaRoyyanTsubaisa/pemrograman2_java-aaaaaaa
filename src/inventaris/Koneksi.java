package inventaris;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Satu class khusus untuk mengatur koneksi Java ke MySQL (JDBC).
 * Semua form dan laporan memakai koneksi yang sama lewat Koneksi.getKoneksi().
 */
public class Koneksi {

    public static final String NAMA_DATABASE = "db_inventaris_toko";
    private static final String URL =
            "jdbc:mysql://localhost:3306/" + NAMA_DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta";
    private static final String USER = "root";
    private static final String PASSWORD = "";   // default Laragon/XAMPP: kosong

    private static Connection koneksi;

    private Koneksi() {
    }

    public static Connection getKoneksi() throws SQLException {
        if (koneksi == null || koneksi.isClosed()) {
            koneksi = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return koneksi;
    }
}
