-- =====================================================================
--  Database  : db_inventaris_toko
--  Aplikasi  : Sistem Inventaris Toko Berbasis Java
--  Mata Kuliah Pemrograman II - Pertemuan 11 s.d. 15
-- =====================================================================

DROP DATABASE IF EXISTS db_inventaris_toko;
CREATE DATABASE db_inventaris_toko CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_inventaris_toko;

-- ---------------------------------------------------------------------
-- Tabel master
-- ---------------------------------------------------------------------
CREATE TABLE kategori (
    id_kategori   INT AUTO_INCREMENT PRIMARY KEY,
    nama_kategori VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE supplier (
    id_supplier   INT AUTO_INCREMENT PRIMARY KEY,
    nama_supplier VARCHAR(100) NOT NULL,
    alamat        VARCHAR(150),
    no_telp       VARCHAR(20)
) ENGINE=InnoDB;

CREATE TABLE barang (
    id_barang   INT AUTO_INCREMENT PRIMARY KEY,
    nama_barang VARCHAR(100) NOT NULL,
    id_kategori INT NOT NULL,
    stok        INT NOT NULL DEFAULT 0,
    harga       DECIMAL(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_barang_kategori FOREIGN KEY (id_kategori)
        REFERENCES kategori(id_kategori) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Tabel transaksi
-- ---------------------------------------------------------------------
CREATE TABLE barang_masuk (
    id_masuk      INT AUTO_INCREMENT PRIMARY KEY,
    id_barang     INT NOT NULL,
    id_supplier   INT NOT NULL,
    tanggal_masuk DATE NOT NULL,
    jumlah        INT NOT NULL,
    CONSTRAINT fk_masuk_barang FOREIGN KEY (id_barang)
        REFERENCES barang(id_barang) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_masuk_supplier FOREIGN KEY (id_supplier)
        REFERENCES supplier(id_supplier) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE barang_keluar (
    id_keluar      INT AUTO_INCREMENT PRIMARY KEY,
    id_barang      INT NOT NULL,
    tanggal_keluar DATE NOT NULL,
    jumlah         INT NOT NULL,
    CONSTRAINT fk_keluar_barang FOREIGN KEY (id_barang)
        REFERENCES barang(id_barang) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Contoh data
-- ---------------------------------------------------------------------
INSERT INTO kategori (nama_kategori) VALUES
('Makanan'), ('Minuman'), ('Kebutuhan Rumah Tangga'), ('Alat Tulis');

INSERT INTO supplier (nama_supplier, alamat, no_telp) VALUES
('PT Sumber Jaya',   'Jakarta',  '081234567890'),
('CV Makmur Abadi',  'Bandung',  '081298765432'),
('UD Sinar Terang',  'Surabaya', '082134567812'),
('PT Kertas Nusantara', 'Bekasi', '085712345678');

-- Stok di bawah sudah memperhitungkan transaksi masuk dan keluar contoh
INSERT INTO barang (nama_barang, id_kategori, stok, harga) VALUES
('Mi Instan Goreng',        1,  65,  3500),
('Air Mineral 600 ml',      2, 140,  3000),
('Kopi Sachet',             2,  80,  1500),
('Biskuit Kelapa',          1,  36,  8500),
('Sabun Cuci Piring 400 ml',3,  24, 12500),
('Deterjen Bubuk 800 g',    3,  18, 21000),
('Buku Tulis 38 Lembar',    4,  90,  4000),
('Pulpen Hitam',            4, 120,  2500);

INSERT INTO barang_masuk (id_barang, id_supplier, tanggal_masuk, jumlah) VALUES
(1, 1, '2026-10-28', 20),
(2, 2, '2026-10-28', 50),
(3, 2, '2026-11-02', 40),
(5, 3, '2026-11-03', 12),
(7, 4, '2026-11-05', 50),
(8, 4, '2026-11-05', 60);

INSERT INTO barang_keluar (id_barang, tanggal_keluar, jumlah) VALUES
(1, '2026-10-28',  5),
(2, '2026-10-28', 10),
(3, '2026-11-04',  8),
(4, '2026-11-06',  4),
(6, '2026-11-07',  2),
(7, '2026-11-09', 10);
