<div align="center">

# 📦 Sistem Inventaris Toko

**Aplikasi desktop Java + MySQL untuk mengelola stok barang, mencatat barang masuk & keluar, dan mencetak laporan siap print / PDF.**

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-5382A1?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-Connector%2FJ%209.1-00758F?style=for-the-badge)
![HTML5](https://img.shields.io/badge/Laporan-HTML%20%2B%20CSS-E34F26?style=for-the-badge&logo=html5&logoColor=white)

Tugas Mata Kuliah **Pemrograman II**: Pertemuan 11 s.d. 15

[Fitur](#-fitur) •
[Screenshot](#-screenshot) •
[Instalasi](#-instalasi--cara-menjalankan) •
[Cara Pakai](#-cara-pakai) •
[Cetak Laporan](#%EF%B8%8F-cara-kerja-fitur-cetak-laporan) •
[Database](#%EF%B8%8F-struktur-database) •
[Pengujian](#-hasil-pengujian)

<img src="dokumentasi/screenshot/01_halaman_laporan_barang.png" alt="Pratinjau laporan data barang" width="820">

</div>

---

## ✨ Fitur

| | Fitur | Keterangan |
|---|---|---|
| 🔌 | **Koneksi JDBC** | Satu class `Koneksi` untuk seluruh aplikasi, jadi pengaturan database cukup di satu tempat |
| 📝 | **CRUD 7 tabel** | Kategori, Supplier, Barang, Pengguna, Barang Masuk, Barang Keluar, Riwayat Harga |
| 👤 | **Pengguna & petugas** | Pilih akun saat aplikasi dibuka, setiap transaksi otomatis mencatat petugasnya. Tab Pengguna khusus admin |
| 🕘 | **Riwayat harga** | Setiap harga barang diubah, harga lama/baru, waktu, dan pengubahnya tercatat otomatis |
| 📈 | **Stok otomatis** | Barang masuk menambah stok, barang keluar mengurangi stok. Ubah/hapus transaksi ikut menyesuaikan stok |
| 🛡️ | **Data aman** | Transaksi database `commit`/`rollback`, stok tidak bisa minus, data yang masih dipakai tidak bisa dihapus (foreign key) |
| 🖨️ | **Cetak Laporan** | Data diambil langsung dari MySQL, disusun menjadi laporan HTML + CSS, lalu dicetak lewat browser |
| 📄 | **Simpan PDF** | Tombol `window.print()` lalu pilih *Simpan sebagai PDF* |
| 📐 | **Layout cetak rapi** | A4 portrait/landscape, margin, header tabel berulang, nomor halaman, tombol tidak ikut tercetak |
| 🇮🇩 | **Format Indonesia** | `Rp 12.500`, `28 Oktober 2026`, `Kamis, 12 November 2026 pukul 21.49 WIB` |

---

## 📸 Screenshot

<table>
  <tr>
    <td align="center"><img src="dokumentasi/screenshot/app_3_barang.png" alt="Form data barang"><br><sub><b>Form CRUD Barang</b></sub></td>
    <td align="center"><img src="dokumentasi/screenshot/crud_4_barang_masuk.png" alt="Form barang masuk"><br><sub><b>Transaksi Barang Masuk + Petugas</b></sub></td>
  </tr>
  <tr>
    <td align="center"><img src="dokumentasi/screenshot/app_7_cetak_laporan.png" alt="Tab cetak laporan"><br><sub><b>Menu Cetak Laporan</b></sub></td>
    <td align="center"><img src="dokumentasi/screenshot/cetak_barang.png" alt="Hasil cetak PDF" width="300"><br><sub><b>Hasil Cetak PDF (A4 Portrait)</b></sub></td>
  </tr>
</table>

<details>
<summary><b>Lihat dialog cetak browser & laporan lainnya</b></summary>
<br>
<img src="dokumentasi/screenshot/04_dialog_cetak_laporan_barang_masuk.png" alt="Dialog cetak Chrome laporan barang masuk">
<img src="dokumentasi/screenshot/02_halaman_laporan_barang_masuk.png" alt="Laporan barang masuk di browser">
<img src="dokumentasi/screenshot/cetak_riwayat_harga.png" alt="Hasil cetak laporan riwayat harga" width="420">
</details>

---

## 🚀 Instalasi & Cara Menjalankan

### Prasyarat

| Kebutuhan | Versi | Catatan |
|---|---|---|
| ☕ **JDK** | 11 atau lebih baru (diuji di JDK 21) | Cek dengan `java -version` |
| 🐬 **MySQL** | 8.x | Paling mudah lewat [Laragon](https://laragon.org) atau [XAMPP](https://www.apachefriends.org) |
| 🌐 **Browser** | Chrome / Edge / Firefox | Untuk menampilkan dan mencetak laporan |

> Driver JDBC (`mysql-connector-j-9.1.0.jar`) **sudah disertakan** di folder `lib/`, jadi tidak perlu unduh lagi.

### Langkah 1: Clone repository

```bash
git clone https://github.com/MaulanaRoyyanTsubaisa/pemrograman2_java-aaaaaaa.git
```

```bash
cd pemrograman2_java-aaaaaaa
```

Atau klik **Code → Download ZIP** lalu ekstrak.

### Langkah 2: Nyalakan MySQL

- **Laragon:** buka Laragon lalu klik **Start All**
- **XAMPP:** buka XAMPP Control Panel lalu klik **Start** pada baris *MySQL*

### Langkah 3: Impor database

Pilih salah satu cara:

<details open>
<summary><b>A. Lewat phpMyAdmin / HeidiSQL (paling mudah)</b></summary>

1. Buka phpMyAdmin (`http://localhost/phpmyadmin`) atau HeidiSQL (Laragon: tombol **Database**).
2. Pilih menu **Import** / **File → Run SQL file**.
3. Pilih file `database/db_inventaris_toko.sql`, lalu jalankan.
4. Database `db_inventaris_toko` beserta 7 tabel dan contoh datanya akan terbentuk.

</details>

<details>
<summary><b>B. Lewat terminal</b></summary>

```bash
mysql -u root < database/db_inventaris_toko.sql
```

</details>

> ⚠️ Script SQL akan **menghapus lalu membuat ulang** database `db_inventaris_toko`. Jangan jalankan ulang jika sudah ada data penting.

### Langkah 4: Sesuaikan koneksi (jika perlu)

Default-nya `root` tanpa password (bawaan Laragon/XAMPP). Jika MySQL kamu memakai password, ubah di
[`src/inventaris/Koneksi.java`](src/inventaris/Koneksi.java):

```java
private static final String USER = "root";
private static final String PASSWORD = "";   // isi password MySQL di sini
```

### Langkah 5: Jalankan aplikasi 🎉

**Windows:** klik dua kali **`run.bat`**

Saat aplikasi terbuka, pilih akun yang dipakai (data contoh: **Administrator** sebagai admin, **Rina Wulandari** / **Budi Santoso** sebagai petugas). Tab *Pengguna* hanya muncul untuk admin.

**Linux / macOS:**

```bash
sh run.sh
```

<details>
<summary><b>Manual (tanpa script)</b></summary>

```bash
javac -encoding UTF-8 -d build -cp "lib/*" src/inventaris/*.java src/inventaris/laporan/*.java
```

```bash
java -cp "build;lib/*" inventaris.MenuUtama
```

Di Linux/macOS ganti `;` menjadi `:` pada classpath.

</details>

<details>
<summary><b>Membuka di NetBeans / IntelliJ</b></summary>

- **NetBeans:** *File → New Project → Java with Ant → Java Project with Existing Sources*, pilih folder `src`.
  Lalu klik kanan **Libraries → Add JAR/Folder** dan pilih `lib/mysql-connector-j-9.1.0.jar`. Main class: `inventaris.MenuUtama`.
- **IntelliJ IDEA:** *File → Open* folder proyek, tandai `src` sebagai *Sources Root*,
  lalu *Project Structure → Libraries → +* dan pilih file `.jar` di folder `lib`.

</details>

---

## 📖 Cara Pakai

Aplikasi memiliki 7 tab (6 untuk petugas):

| Tab | Fungsi |
|---|---|
| **Kategori** | Kelola kategori barang (Makanan, Minuman, ...) |
| **Supplier** | Kelola data pemasok: nama, alamat, no. telepon |
| **Barang** | Kelola barang: nama, kategori, stok awal, harga. Perubahan harga tercatat di riwayat harga |
| **Barang Masuk** | Catat barang datang dari supplier, **stok bertambah otomatis** |
| **Barang Keluar** | Catat barang terjual/keluar, **stok berkurang otomatis** (ditolak bila stok kurang) |
| **Pengguna** | Kelola akun admin/petugas *(khusus admin)* |
| **Cetak Laporan** | Pilih laporan, lalu klik **Cetak Laporan** |

### Operasi CRUD

1. **Tambah:** isi form, lalu klik **Simpan**.
2. **Lihat:** data tampil otomatis di tabel bawah (dimuat ulang setiap tab dibuka).
3. **Ubah:** klik baris di tabel (form terisi otomatis), ubah isinya, lalu klik **Ubah**.
4. **Hapus:** klik baris, lalu klik **Hapus** dan konfirmasi.
5. **Bersihkan:** mengosongkan form.

### Mencetak laporan

```
Tab "Cetak Laporan"  →  pilih laporan  →  klik [Cetak Laporan]
        ↓
Browser terbuka menampilkan laporan  →  klik tombol kuning [🖨 Cetak Laporan]
        ↓
Dialog cetak  →  pilih printer  ATAU  "Simpan sebagai PDF"
```

> 💡 **Tips:** pada dialog cetak, buka *Setelan lainnya* lalu **matikan "Header dan footer"** agar alamat file tidak ikut tercetak.
> Pastikan juga **"Grafik latar belakang"** aktif supaya warna header tabel ikut tercetak.

Laporan yang tersedia:

| Laporan | Tabel | Kertas | Isi |
|---|---|---|---|
| Data Kategori | `kategori` | A4 Portrait | Jumlah jenis barang & total stok per kategori |
| Data Supplier | `supplier` | A4 Portrait | Alamat, telepon, jumlah transaksi masuk |
| Data Barang | `barang` | A4 Portrait | Stok, harga, nilai persediaan + total |
| Barang Masuk | `barang_masuk` | A4 Landscape | Tanggal, barang, supplier, petugas, jumlah, subtotal + total |
| Barang Keluar | `barang_keluar` | A4 Landscape | Tanggal, barang, petugas, jumlah, subtotal + total |
| Riwayat Harga | `riwayat_harga` | A4 Portrait | Harga lama, harga baru, selisih, diubah oleh |
| Data Pengguna | `user` | A4 Portrait | Username, role, jumlah transaksi masuk/keluar |

---

## 🖨️ Cara Kerja Fitur Cetak Laporan

```mermaid
flowchart LR
    A[👤 Pengguna klik<br/>Cetak Laporan] --> B[PanelLaporan.java]
    B --> C[LaporanGenerator.java]
    C -->|query SQL via JDBC| D[(MySQL<br/>db_inventaris_toko)]
    D -->|ResultSet| C
    C -->|nomor urut, format Rp & tanggal,<br/>total, HTML + CSS| E[📄 laporan/*.html]
    E -->|Desktop.browse| F[🌐 Browser]
    F -->|window.print| G[🖨️ Printer / PDF]
```

**1. Query SQL:** setiap laporan didefinisikan di [`JenisLaporan.java`](src/inventaris/laporan/JenisLaporan.java). Contoh laporan barang:

```sql
SELECT b.id_barang, b.nama_barang, k.nama_kategori, b.stok, b.harga,
       (b.stok * b.harga) AS nilai_persediaan
FROM barang b
JOIN kategori k ON k.id_kategori = b.id_kategori
ORDER BY b.id_barang;
```

**2. Hasil query dibuat menjadi HTML:** [`LaporanGenerator.java`](src/inventaris/laporan/LaporanGenerator.java) membaca `ResultSet` baris per baris, memberi nomor urut, memformat angka/tanggal, menghitung total, lalu menulis file HTML lengkap dengan:

- Kop: logo, nama toko, nama aplikasi
- Judul laporan, tanggal cetak, sumber data, jumlah data
- Tabel data + baris **TOTAL**
- Kolom tanda tangan *"Mengetahui, Kepala Gudang"*

**3. Tombol cetak:**

```html
<div class="toolbar no-print">
    <button type="button" onclick="window.print()">🖨 Cetak Laporan</button>
</div>
```

**4. Pengaturan halaman cetak:**

```css
@page {
    size: A4 portrait;                 /* landscape untuk laporan transaksi */
    margin: 15mm 12mm 18mm 12mm;
    @bottom-right { content: "Halaman " counter(page) " dari " counter(pages); }
}
@media print {
    .no-print { display: none !important; }           /* tombol tidak ikut tercetak */
    * { print-color-adjust: exact; }                   /* warna header tetap tercetak */
    table.data thead { display: table-header-group; }  /* header tabel diulang tiap halaman */
    table.data tr { break-inside: avoid; }             /* baris tidak terpotong */
}
```

> 🎨 Ingin mengganti nama toko di kop laporan? Ubah konstanta `NAMA_INSTANSI`, `NAMA_APLIKASI`, dan `PENANDATANGAN` di bagian atas `LaporanGenerator.java`.

---

## 🗄️ Struktur Database

```mermaid
erDiagram
    KATEGORI ||--o{ BARANG : "mengelompokkan"
    BARANG ||--o{ BARANG_MASUK : "dicatat di"
    BARANG ||--o{ BARANG_KELUAR : "dicatat di"
    SUPPLIER ||--o{ BARANG_MASUK : "memasok"
    USER ||--o{ BARANG_MASUK : "mencatat"
    USER ||--o{ BARANG_KELUAR : "mencatat"
    BARANG ||--o{ RIWAYAT_HARGA : "punya"
    USER |o--o{ RIWAYAT_HARGA : "mengubah"

    KATEGORI {
        int id_kategori PK
        varchar nama_kategori
    }
    SUPPLIER {
        int id_supplier PK
        varchar nama_supplier
        varchar alamat
        varchar no_telp
    }
    BARANG {
        int id_barang PK
        varchar nama_barang
        int id_kategori FK
        int stok
        decimal harga
    }
    USER {
        int id_user PK
        varchar nama_user
        varchar username UK
        enum role
    }
    BARANG_MASUK {
        int id_masuk PK
        int id_barang FK
        int id_supplier FK
        int id_user FK
        date tanggal_masuk
        int jumlah
    }
    BARANG_KELUAR {
        int id_keluar PK
        int id_barang FK
        int id_user FK
        date tanggal_keluar
        int jumlah
    }
    RIWAYAT_HARGA {
        int id_riwayat PK
        int id_barang FK
        int id_user FK
        decimal harga_lama
        decimal harga_baru
        datetime tanggal_ubah
    }
```

- Dinormalisasi sampai **3NF**: kategori dan supplier tidak disimpan berulang, cukup dihubungkan dengan foreign key.
- `ON DELETE RESTRICT`: kategori/supplier/barang yang masih dipakai **tidak bisa dihapus**.
- Harga memakai `DECIMAL(12,2)` agar nilai uang tepat.
- **Perbaikan Pertemuan 12:** tabel `user` + `id_user` pada transaksi (siapa yang mencatat), dan tabel `riwayat_harga` (perubahan harga dari waktu ke waktu).

---

## 📁 Struktur Proyek

```
📦 InventarisToko
├── 📄 run.bat / run.sh            ← jalankan aplikasi
├── 📂 database
│   └── db_inventaris_toko.sql     ← struktur 7 tabel + contoh data
├── 📂 lib
│   └── mysql-connector-j-9.1.0.jar
├── 📂 src/inventaris
│   ├── Koneksi.java               ← koneksi JDBC ke MySQL
│   ├── MenuUtama.java             ← jendela utama + pilih pengguna (main class)
│   ├── Sesi.java                  ← pengguna yang sedang login
│   ├── FormDasar.java             ← kerangka form CRUD
│   ├── FormKategori.java          ┐
│   ├── FormSupplier.java          │
│   ├── FormBarang.java            ├ CRUD tiap tabel
│   ├── FormUser.java              │
│   ├── FormBarangMasuk.java       │
│   ├── FormBarangKeluar.java      ┘
│   ├── Stok.java                  ← transaksi & konsistensi stok
│   ├── PanelLaporan.java          ← tab Cetak Laporan
│   ├── Util.java
│   └── 📂 laporan
│       ├── JenisLaporan.java      ← query SQL tiap laporan
│       ├── Kolom.java             ← definisi kolom laporan
│       └── LaporanGenerator.java  ← HTML + CSS + buka browser
├── 📂 laporan                     ← hasil laporan HTML
└── 📂 dokumentasi
    ├── Dokumentasi Pertemuan 13 - Database dan CRUD.docx / .pdf
    ├── Dokumentasi Pertemuan 14-15 - Cetak Laporan.docx / .pdf
    ├── 📂 pdf                     ← hasil cetak 7 laporan
    └── 📂 screenshot
```

---

## ✅ Hasil Pengujian

<details open>
<summary><b>Koneksi, CRUD & konsistensi stok (28/28 berhasil)</b></summary>

Diuji otomatis dengan menjalankan **kode form asli** (mengisi input, menekan Simpan/Ubah/Hapus) lalu memeriksa database.

| Tabel | Contoh skenario | Hasil |
|---|---|---|
| koneksi | Java terhubung ke `db_inventaris_toko` | ✅ 1/1 |
| kategori | Create, Update, nama kosong ditolak, hapus kategori yang masih dipakai ditolak | ✅ 5/5 |
| supplier | Create, Update, telepon berisi huruf ditolak | ✅ 4/4 |
| barang | Ubah harga 45.000 → 47.500 tercatat di `riwayat_harga`, harga negatif ditolak | ✅ 5/5 |
| user | Username ganda ditolak, pengguna yang punya transaksi / sedang login tidak bisa dihapus | ✅ 6/6 |
| barang_masuk | Stok 10 → 15 → 18 (ubah) → 10 (hapus), petugas tercatat, tanggal salah ditolak | ✅ 4/4 |
| barang_keluar | Stok 18 → 15, keluar 100 unit (melebihi stok) ditolak tanpa mengubah data | ✅ 3/3 |

</details>

<details open>
<summary><b>Kesesuaian laporan dengan database</b></summary>

| Laporan | Database | Laporan | |
|---|---|---|---|
| Data Barang | 8 data, stok 573, Rp 2.411.500 | 8 data, stok 573, Rp 2.411.500 | ✅ |
| Barang Masuk | 6 data, 232 unit, Rp 780.000 | 6 data, 232 unit, Rp 780.000 | ✅ |
| Barang Keluar | 6 data, 39 unit, Rp 175.500 | 6 data, 39 unit, Rp 175.500 | ✅ |
| Kategori / Supplier | 4 / 4 data | 4 / 4 data | ✅ |
| Riwayat Harga / Pengguna | 2 / 3 data | 2 / 3 data | ✅ |

</details>

<details open>
<summary><b>Pencetakan</b></summary>

- ✅ Tombol **Cetak Laporan** membuka dialog cetak Chrome dengan pratinjau yang benar
- ✅ Tombol & toolbar **tidak ikut tercetak**
- ✅ Ukuran kertas A4 portrait (595×842 pt) & landscape (842×595 pt)
- ✅ Warna header tabel ikut tercetak, nomor halaman tampil
- ✅ 7 laporan berhasil disimpan sebagai PDF ([lihat folder `dokumentasi/pdf`](dokumentasi/pdf))

</details>

📘 Dokumentasi lengkap:
- [**Dokumentasi Pertemuan 13 – Database dan CRUD.pdf**](dokumentasi/Dokumentasi%20Pertemuan%2013%20-%20Database%20dan%20CRUD.pdf)
- [**Dokumentasi Pertemuan 14-15 – Cetak Laporan.pdf**](dokumentasi/Dokumentasi%20Pertemuan%2014-15%20-%20Cetak%20Laporan.pdf)

---

## 🛠️ Troubleshooting

| Masalah | Solusi |
|---|---|
| `Communications link failure` | MySQL belum menyala. Start MySQL di Laragon/XAMPP |
| `Unknown database 'db_inventaris_toko'` | Database belum diimpor (lihat [Langkah 3](#langkah-3-impor-database)) |
| `Access denied for user 'root'` | Isi `PASSWORD` di `Koneksi.java` sesuai password MySQL kamu |
| `No suitable driver found` | Pastikan file `.jar` ada di folder `lib/` dan ikut di classpath |
| `'javac' is not recognized` | JDK belum terpasang / belum masuk PATH |
| `Table 'db_inventaris_toko.user' doesn't exist` | Database versi lama. Impor ulang `db_inventaris_toko.sql` |
| Warna tabel tidak tercetak | Aktifkan **Grafik latar belakang** di dialog cetak |
| Alamat file ikut tercetak | Matikan **Header dan footer** di dialog cetak |

---

<div align="center">

**Pemrograman II** · Dosen Pengampu: **Niki Ratama, M.Kom.**

Dibuat oleh **Maulana Royyan Tsubaisa** · 2026

</div>
