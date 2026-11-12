@echo off
rem Kompilasi lalu jalankan Sistem Inventaris Toko
cd /d "%~dp0"
if not exist build mkdir build
javac -encoding UTF-8 -d build -cp "lib\*" src\inventaris\*.java src\inventaris\laporan\*.java || goto :error
java -cp "build;lib\*" inventaris.MenuUtama
goto :eof
:error
echo Kompilasi gagal.
pause
