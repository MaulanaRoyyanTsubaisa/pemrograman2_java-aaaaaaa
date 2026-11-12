#!/bin/sh
# Kompilasi lalu jalankan Sistem Inventaris Toko (Linux / macOS)
cd "$(dirname "$0")" || exit 1
mkdir -p build
javac -encoding UTF-8 -d build -cp "lib/*" src/inventaris/*.java src/inventaris/laporan/*.java || { echo "Kompilasi gagal."; exit 1; }
java -cp "build:lib/*" inventaris.MenuUtama
