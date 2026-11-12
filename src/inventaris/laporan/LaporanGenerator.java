package inventaris.laporan;

import inventaris.Koneksi;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Fitur Cetak Laporan.
 * 1. Mengambil data dari MySQL dengan query SQL (JenisLaporan.sql).
 * 2. Menyusun hasil query menjadi halaman HTML + CSS (identitas, judul, tanggal cetak, nomor urut).
 * 3. Menyimpan file .html ke folder "laporan" lalu membukanya di browser.
 * 4. Di browser, tombol "Cetak Laporan" memanggil window.print() untuk mencetak / Simpan sebagai PDF.
 */
public class LaporanGenerator {

    // ---- Identitas laporan (silakan sesuaikan dengan kelompok/toko masing-masing) ----
    public static final String NAMA_APLIKASI = "Sistem Inventaris Toko";
    public static final String NAMA_INSTANSI = "TOKO SEJAHTERA";
    public static final String KETERANGAN_INSTANSI = "Aplikasi Inventaris Barang Berbasis Java dan MySQL";
    public static final String PENANDATANGAN = "Kepala Gudang";

    private static final Locale ID = Locale.forLanguageTag("id-ID");
    private static final DateTimeFormatter FMT_TANGGAL = DateTimeFormatter.ofPattern("d MMMM yyyy", ID);
    private static final DateTimeFormatter FMT_CETAK =
            DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy 'pukul' HH.mm 'WIB'", ID);
    private static final DateTimeFormatter FMT_FILE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final Path folderOutput;

    public LaporanGenerator(Path folderOutput) {
        this.folderOutput = folderOutput;
    }

    public LaporanGenerator() {
        this(Paths.get("laporan"));
    }

    /** Membuat file laporan HTML dari database dan mengembalikan lokasinya. */
    public Path buat(JenisLaporan jenis) throws SQLException, IOException {
        LocalDateTime waktuCetak = LocalDateTime.now();
        StringBuilder baris = new StringBuilder();
        BigDecimal[] total = new BigDecimal[jenis.kolom.length];
        int jumlahData = 0;

        Connection c = Koneksi.getKoneksi();
        try (PreparedStatement ps = c.prepareStatement(jenis.sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                jumlahData++;
                baris.append("        <tr>\n");
                baris.append("          <td class=\"no\">").append(jumlahData).append("</td>\n");
                for (int i = 0; i < jenis.kolom.length; i++) {
                    Kolom k = jenis.kolom[i];
                    Object nilai = rs.getObject(k.field);
                    if (k.dijumlah && nilai != null) {
                        BigDecimal n = new BigDecimal(nilai.toString());
                        total[i] = total[i] == null ? n : total[i].add(n);
                    }
                    baris.append("          <td").append(k.rataKanan() ? " class=\"angka\"" : "").append(">")
                         .append(format(k, nilai)).append("</td>\n");
                }
                baris.append("        </tr>\n");
            }
        }

        String html = susunHtml(jenis, baris.toString(), total, jumlahData, waktuCetak);
        Files.createDirectories(folderOutput);
        Path file = folderOutput.resolve("laporan_" + jenis.tabel + "_" + FMT_FILE.format(waktuCetak) + ".html");
        Files.write(file, html.getBytes(StandardCharsets.UTF_8));
        return file.toAbsolutePath();
    }

    /** Membuka file laporan di browser default. */
    public static void bukaDiBrowser(Path file) throws IOException {
        File f = file.toFile();
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(f.toURI());
        } else {
            new ProcessBuilder("cmd", "/c", "start", "\"\"", f.getAbsolutePath()).start();
        }
    }

    // ------------------------------------------------------------------------------

    private String susunHtml(JenisLaporan jenis, String isiTabel, BigDecimal[] total,
                             int jumlahData, LocalDateTime waktuCetak) {
        int jumlahKolom = jenis.kolom.length + 1;
        String orientasi = jenis.landscape ? "landscape" : "portrait";
        StringBuilder h = new StringBuilder();

        h.append("<!DOCTYPE html>\n<html lang=\"id\">\n<head>\n")
         .append("  <meta charset=\"UTF-8\">\n")
         .append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n")
         .append("  <title>").append(esc(jenis.judul)).append(" - ").append(esc(NAMA_APLIKASI)).append("</title>\n")
         .append("  <style>\n").append(CSS)
         .append("    @page { size: A4 ").append(orientasi).append("; }\n")
         .append("  </style>\n</head>\n<body>\n\n");

        // Tombol cetak (class no-print -> tidak ikut tercetak)
        h.append("  <div class=\"toolbar no-print\">\n")
         .append("    <div>\n")
         .append("      <strong>Pratinjau Laporan</strong>\n")
         .append("      <span>Kertas A4 ").append(jenis.landscape ? "Landscape" : "Portrait")
         .append(" &middot; pilih <em>Simpan sebagai PDF</em> pada dialog cetak untuk menyimpan PDF.</span>\n")
         .append("    </div>\n")
         .append("    <button type=\"button\" onclick=\"window.print()\">&#128424; Cetak Laporan</button>\n")
         .append("  </div>\n\n");

        h.append("  <main class=\"kertas ").append(orientasi).append("\">\n");

        // Kop / identitas laporan
        h.append("    <header class=\"kop\">\n")
         .append("      <div class=\"logo\">ST</div>\n")
         .append("      <div class=\"identitas\">\n")
         .append("        <h1>").append(esc(NAMA_INSTANSI)).append("</h1>\n")
         .append("        <p class=\"aplikasi\">").append(esc(NAMA_APLIKASI)).append("</p>\n")
         .append("        <p>").append(esc(KETERANGAN_INSTANSI)).append("</p>\n")
         .append("      </div>\n")
         .append("    </header>\n\n");

        h.append("    <h2 class=\"judul\">").append(esc(jenis.judul.toUpperCase(ID))).append("</h2>\n");

        h.append("    <table class=\"info\">\n")
         .append("      <tr><td>Tanggal Cetak</td><td>:</td><td>").append(esc(FMT_CETAK.format(waktuCetak)))
         .append("</td></tr>\n")
         .append("      <tr><td>Sumber Data</td><td>:</td><td>Database <b>").append(Koneksi.NAMA_DATABASE)
         .append("</b>, tabel <b>").append(jenis.tabel).append("</b></td></tr>\n")
         .append("      <tr><td>Jumlah Data</td><td>:</td><td>").append(jumlahData).append(" baris</td></tr>\n")
         .append("    </table>\n\n");

        // Tabel data
        h.append("    <table class=\"data\">\n      <thead>\n        <tr>\n          <th class=\"no\">No</th>\n");
        for (Kolom k : jenis.kolom) {
            h.append("          <th").append(k.rataKanan() ? " class=\"angka\"" : "").append(">")
             .append(esc(k.judul)).append("</th>\n");
        }
        h.append("        </tr>\n      </thead>\n      <tbody>\n");
        if (jumlahData == 0) {
            h.append("        <tr><td class=\"kosong\" colspan=\"").append(jumlahKolom)
             .append("\">Belum ada data pada tabel ini.</td></tr>\n");
        } else {
            h.append(isiTabel);
        }
        h.append("      </tbody>\n");

        if (adaTotal(total) && jumlahData > 0) {
            h.append("      <tfoot>\n        <tr>\n");
            int labelSpan = 1;
            while (labelSpan <= jenis.kolom.length && !jenis.kolom[labelSpan - 1].dijumlah) {
                labelSpan++;
            }
            h.append("          <td colspan=\"").append(labelSpan).append("\">TOTAL</td>\n");
            for (int i = labelSpan - 1; i < jenis.kolom.length; i++) {
                Kolom k = jenis.kolom[i];
                h.append("          <td class=\"angka\">")
                 .append(k.dijumlah ? format(k, total[i]) : "").append("</td>\n");
            }
            h.append("        </tr>\n      </tfoot>\n");
        }
        h.append("    </table>\n\n");

        if (jenis.catatan != null) {
            h.append("    <p class=\"catatan\">Catatan: ").append(esc(jenis.catatan)).append("</p>\n\n");
        }

        // Tanda tangan
        h.append("    <section class=\"ttd\">\n")
         .append("      <p>").append(esc(FMT_TANGGAL.format(waktuCetak))).append("</p>\n")
         .append("      <p>Mengetahui,<br>").append(esc(PENANDATANGAN)).append("</p>\n")
         .append("      <div class=\"ruang-ttd\"></div>\n")
         .append("      <p class=\"nama\">(..............................)</p>\n")
         .append("    </section>\n\n");

        h.append("    <footer class=\"kaki\">Dokumen ini dicetak otomatis dari ").append(esc(NAMA_APLIKASI))
         .append(" &middot; ").append(esc(FMT_CETAK.format(waktuCetak))).append("</footer>\n");

        h.append("  </main>\n</body>\n</html>\n");
        return h.toString();
    }

    private static boolean adaTotal(BigDecimal[] total) {
        for (BigDecimal b : total) {
            if (b != null) {
                return true;
            }
        }
        return false;
    }

    private static String format(Kolom k, Object nilai) {
        if (nilai == null) {
            return "-";
        }
        switch (k.tipe) {
            case ANGKA:
                return NumberFormat.getIntegerInstance(ID).format(new BigDecimal(nilai.toString()));
            case RUPIAH: {
                NumberFormat nf = NumberFormat.getNumberInstance(ID);
                nf.setMinimumFractionDigits(0);
                nf.setMaximumFractionDigits(2);
                return "Rp " + nf.format(new BigDecimal(nilai.toString()));
            }
            case TANGGAL: {
                LocalDate tgl = nilai instanceof java.sql.Date
                        ? ((java.sql.Date) nilai).toLocalDate()
                        : nilai instanceof LocalDate ? (LocalDate) nilai : LocalDate.parse(nilai.toString());
                return FMT_TANGGAL.format(tgl);
            }
            default:
                return esc(nilai.toString());
        }
    }

    /** Mencegah karakter khusus (&lt; &gt; &amp;) merusak HTML. */
    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    // ---- Desain laporan (CSS) -----------------------------------------------------
    private static final String CSS = String.join("\n",
        "    :root { --utama: #1f3a5f; --garis: #9aa7b5; --zebra: #f3f6f9; }",
        "    * { box-sizing: border-box; }",
        "    body { margin: 0; background: #e5e8ec; color: #1b1f24;",
        "           font-family: Arial, Helvetica, sans-serif; font-size: 12px; }",
        "",
        "    /* ---------- tampilan di layar ---------- */",
        "    .toolbar { position: sticky; top: 0; z-index: 10; display: flex; align-items: center;",
        "               justify-content: space-between; gap: 16px; padding: 12px 24px;",
        "               background: var(--utama); color: #fff; box-shadow: 0 2px 6px rgba(0,0,0,.25); }",
        "    .toolbar strong { display: block; font-size: 15px; }",
        "    .toolbar span { font-size: 12px; opacity: .85; }",
        "    .toolbar button { flex: none; padding: 10px 22px; border: 0; border-radius: 6px; cursor: pointer;",
        "                      background: #f2b134; color: #1b1f24; font-size: 14px; font-weight: bold; }",
        "    .toolbar button:hover { background: #ffc857; }",
        "    .kertas { margin: 24px auto; padding: 15mm 15mm 12mm; background: #fff;",
        "              box-shadow: 0 4px 18px rgba(0,0,0,.18); }",
        "    .kertas.portrait  { width: 210mm; min-height: 297mm; }",
        "    .kertas.landscape { width: 297mm; min-height: 210mm; }",
        "    @media screen and (max-width: 1100px) {",
        "      .kertas.portrait, .kertas.landscape { width: auto; min-height: 0; margin: 12px; padding: 16px; }",
        "      .toolbar { flex-wrap: wrap; padding: 12px 16px; }",
        "      table.data { display: block; overflow-x: auto; }",
        "    }",
        "",
        "    /* ---------- kop laporan ---------- */",
        "    .kop { display: flex; align-items: center; gap: 16px; padding-bottom: 10px;",
        "           border-bottom: 3px double var(--utama); }",
        "    .logo { flex: none; width: 58px; height: 58px; border-radius: 10px; background: var(--utama);",
        "            color: #fff; font: bold 22px/58px Arial, sans-serif; text-align: center; }",
        "    .identitas h1 { margin: 0; font-size: 20px; letter-spacing: 1px; color: var(--utama); }",
        "    .identitas p { margin: 2px 0 0; color: #4a5562; }",
        "    .identitas .aplikasi { font-weight: bold; color: #1b1f24; font-size: 13px; }",
        "    .judul { margin: 16px 0 10px; text-align: center; font-size: 16px; letter-spacing: .5px;",
        "             text-decoration: underline; text-underline-offset: 4px; }",
        "    table.info { margin-bottom: 12px; border-collapse: collapse; }",
        "    table.info td { padding: 2px 6px 2px 0; vertical-align: top; }",
        "    table.info td:first-child { width: 95px; color: #4a5562; }",
        "",
        "    /* ---------- tabel data ---------- */",
        "    table.data { width: 100%; border-collapse: collapse; }",
        "    table.data th, table.data td { border: 1px solid var(--garis); padding: 6px 8px; text-align: left; }",
        "    table.data thead th { background: var(--utama); color: #fff; font-weight: bold;",
        "                          border-color: var(--utama); }",
        "    table.data tbody tr:nth-child(even) td { background: var(--zebra); }",
        "    table.data tfoot td { background: #dfe6ee; font-weight: bold; }",
        "    table.data .no { width: 34px; text-align: center; }",
        "    table.data .angka { text-align: right; white-space: nowrap; }",
        "    table.data .kosong { text-align: center; padding: 18px; color: #6b7682; font-style: italic; }",
        "    .catatan { margin: 6px 0 0; font-size: 10.5px; color: #4a5562; font-style: italic; }",
        "",
        "    /* ---------- tanda tangan & kaki ---------- */",
        "    .ttd { width: 220px; margin: 28px 0 0 auto; text-align: center; break-inside: avoid; }",
        "    .ttd p { margin: 0 0 4px; }",
        "    .ruang-ttd { height: 64px; }",
        "    .kaki { margin-top: 24px; padding-top: 6px; border-top: 1px solid #ccd3db;",
        "            font-size: 10px; color: #6b7682; text-align: center; }",
        "",
        "    /* ---------- pengaturan halaman cetak ---------- */",
        "    @page {",
        "      margin: 15mm 12mm 18mm 12mm;",
        "      @bottom-right { content: \"Halaman \" counter(page) \" dari \" counter(pages);",
        "                      font: 9px Arial, sans-serif; color: #6b7682; }",
        "    }",
        "    @media print {",
        "      .no-print { display: none !important; }",
        "      body { background: #fff; font-size: 11px; }",
        "      .kertas, .kertas.portrait, .kertas.landscape { width: auto; min-height: 0; margin: 0;",
        "                                                     padding: 0; box-shadow: none; }",
        "      * { -webkit-print-color-adjust: exact; print-color-adjust: exact; }",
        "      table.data thead { display: table-header-group; }  /* header tabel diulang tiap halaman */",
        "      table.data tfoot { display: table-row-group; }",
        "      table.data tr { break-inside: avoid; }",
        "      .kaki { position: static; }",
        "    }",
        "");

    /** Menjalankan pembuatan semua laporan tanpa GUI (untuk pengujian). */
    public static void main(String[] args) throws Exception {
        LaporanGenerator gen = new LaporanGenerator();
        for (JenisLaporan j : JenisLaporan.values()) {
            System.out.println("Dibuat: " + gen.buat(j));
        }
    }
}
