package inventaris.laporan;

/**
 * Daftar laporan yang bisa dicetak. Setiap laporan berisi query SQL yang mengambil
 * SELURUH data dari satu tabel database (dengan JOIN untuk menampilkan nama, bukan hanya ID).
 */
public enum JenisLaporan {

    KATEGORI("kategori", "Laporan Data Kategori", false,
            "SELECT k.id_kategori, k.nama_kategori, "
            + "       COUNT(b.id_barang)       AS jumlah_barang, "
            + "       COALESCE(SUM(b.stok), 0) AS total_stok "
            + "FROM kategori k "
            + "LEFT JOIN barang b ON b.id_kategori = k.id_kategori "
            + "GROUP BY k.id_kategori, k.nama_kategori "
            + "ORDER BY k.id_kategori",
            null,
            Kolom.teks("ID Kategori", "id_kategori"),
            Kolom.teks("Nama Kategori", "nama_kategori"),
            Kolom.angka("Jumlah Jenis Barang", "jumlah_barang", true),
            Kolom.angka("Total Stok", "total_stok", true)),

    SUPPLIER("supplier", "Laporan Data Supplier", false,
            "SELECT s.id_supplier, s.nama_supplier, s.alamat, s.no_telp, "
            + "       COUNT(m.id_masuk) AS jumlah_transaksi "
            + "FROM supplier s "
            + "LEFT JOIN barang_masuk m ON m.id_supplier = s.id_supplier "
            + "GROUP BY s.id_supplier, s.nama_supplier, s.alamat, s.no_telp "
            + "ORDER BY s.id_supplier",
            null,
            Kolom.teks("ID", "id_supplier"),
            Kolom.teks("Nama Supplier", "nama_supplier"),
            Kolom.teks("Alamat", "alamat"),
            Kolom.teks("No. Telepon", "no_telp"),
            Kolom.angka("Transaksi Masuk", "jumlah_transaksi", true)),

    BARANG("barang", "Laporan Data Barang", false,
            "SELECT b.id_barang, b.nama_barang, k.nama_kategori, b.stok, b.harga, "
            + "       (b.stok * b.harga) AS nilai_persediaan "
            + "FROM barang b "
            + "JOIN kategori k ON k.id_kategori = b.id_kategori "
            + "ORDER BY b.id_barang",
            "Nilai persediaan = stok x harga satuan.",
            Kolom.teks("ID", "id_barang"),
            Kolom.teks("Nama Barang", "nama_barang"),
            Kolom.teks("Kategori", "nama_kategori"),
            Kolom.angka("Stok", "stok", true),
            Kolom.rupiah("Harga Satuan", "harga", false),
            Kolom.rupiah("Nilai Persediaan", "nilai_persediaan", true)),

    BARANG_MASUK("barang_masuk", "Laporan Barang Masuk", true,
            "SELECT m.id_masuk, m.tanggal_masuk, b.nama_barang, k.nama_kategori, "
            + "       s.nama_supplier, m.jumlah, b.harga, (m.jumlah * b.harga) AS subtotal "
            + "FROM barang_masuk m "
            + "JOIN barang b   ON b.id_barang   = m.id_barang "
            + "JOIN kategori k ON k.id_kategori = b.id_kategori "
            + "JOIN supplier s ON s.id_supplier = m.id_supplier "
            + "ORDER BY m.tanggal_masuk, m.id_masuk",
            "Harga satuan mengikuti harga barang saat laporan dicetak.",
            Kolom.teks("ID", "id_masuk"),
            Kolom.tanggal("Tanggal Masuk", "tanggal_masuk"),
            Kolom.teks("Nama Barang", "nama_barang"),
            Kolom.teks("Kategori", "nama_kategori"),
            Kolom.teks("Supplier", "nama_supplier"),
            Kolom.angka("Jumlah", "jumlah", true),
            Kolom.rupiah("Harga Satuan", "harga", false),
            Kolom.rupiah("Subtotal", "subtotal", true)),

    BARANG_KELUAR("barang_keluar", "Laporan Barang Keluar", true,
            "SELECT k.id_keluar, k.tanggal_keluar, b.nama_barang, kt.nama_kategori, "
            + "       k.jumlah, b.harga, (k.jumlah * b.harga) AS subtotal "
            + "FROM barang_keluar k "
            + "JOIN barang b    ON b.id_barang    = k.id_barang "
            + "JOIN kategori kt ON kt.id_kategori = b.id_kategori "
            + "ORDER BY k.tanggal_keluar, k.id_keluar",
            "Harga satuan mengikuti harga barang saat laporan dicetak.",
            Kolom.teks("ID", "id_keluar"),
            Kolom.tanggal("Tanggal Keluar", "tanggal_keluar"),
            Kolom.teks("Nama Barang", "nama_barang"),
            Kolom.teks("Kategori", "nama_kategori"),
            Kolom.angka("Jumlah", "jumlah", true),
            Kolom.rupiah("Harga Satuan", "harga", false),
            Kolom.rupiah("Subtotal", "subtotal", true));

    final String tabel;
    final String judul;
    final boolean landscape;
    final String sql;
    final String catatan;
    final Kolom[] kolom;

    JenisLaporan(String tabel, String judul, boolean landscape, String sql, String catatan, Kolom... kolom) {
        this.tabel = tabel;
        this.judul = judul;
        this.landscape = landscape;
        this.sql = sql;
        this.catatan = catatan;
        this.kolom = kolom;
    }

    public String getJudul() {
        return judul;
    }

    @Override
    public String toString() {
        return judul + "  (tabel " + tabel + ")";
    }
}
