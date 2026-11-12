package inventaris.laporan;

/** Definisi satu kolom pada tabel laporan. */
public class Kolom {

    public enum Tipe { TEKS, ANGKA, RUPIAH, TANGGAL }

    final String judul;     // teks header kolom
    final String field;     // nama kolom hasil query SQL
    final Tipe tipe;
    final boolean dijumlah; // tampilkan total di baris paling bawah

    private Kolom(String judul, String field, Tipe tipe, boolean dijumlah) {
        this.judul = judul;
        this.field = field;
        this.tipe = tipe;
        this.dijumlah = dijumlah;
    }

    public static Kolom teks(String judul, String field) {
        return new Kolom(judul, field, Tipe.TEKS, false);
    }

    public static Kolom tanggal(String judul, String field) {
        return new Kolom(judul, field, Tipe.TANGGAL, false);
    }

    public static Kolom angka(String judul, String field, boolean dijumlah) {
        return new Kolom(judul, field, Tipe.ANGKA, dijumlah);
    }

    public static Kolom rupiah(String judul, String field, boolean dijumlah) {
        return new Kolom(judul, field, Tipe.RUPIAH, dijumlah);
    }

    boolean rataKanan() {
        return tipe == Tipe.ANGKA || tipe == Tipe.RUPIAH;
    }
}
