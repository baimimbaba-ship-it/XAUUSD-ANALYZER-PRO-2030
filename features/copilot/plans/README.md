# Rencana Pengembangan XAUUSD Analyzer Pro 2030

Dokumen ini mencatat rencana pengembangan berdasarkan kondisi repository saat ini: aplikasi Android Kotlin masih berfokus pada snapshot harga XAUUSD dari BiQuote, sementara penyimpanan riwayat sinyal masih berupa TODO. Rencana ini bukan klaim bahwa fitur-fitur di bawah sudah tersedia.

## Tujuan

Membangun aplikasi pemantau XAUUSD yang informatif dan transparan tentang sumber serta kesegaran data. Informasi analisis adalah alat bantu edukasi, bukan nasihat atau jaminan hasil trading.

## Tahapan prioritas

### 1. Keandalan data pasar
- Tampilkan harga, bid/ask, spread, sumber data, waktu penerimaan, status koneksi, dan status LIVE/STALE secara konsisten.
- Tangani respons API yang tidak lengkap, kesalahan jaringan, pembatasan permintaan, dan pemulihan koneksi tanpa menampilkan harga lama sebagai harga live.
- Hindari polling ketika aplikasi berada di latar belakang; sediakan refresh manual dan indikator waktu pembaruan.
- Uji parsing respons serta penanganan data stale dan kegagalan jaringan dengan unit test.

**Selesai jika:** kondisi LIVE hanya ditampilkan untuk data yang memenuhi aturan kesegaran yang jelas, dan kegagalan data tidak disamarkan sebagai harga terkini.

### 2. Grafik dan analisis teknikal
- Tentukan terlebih dahulu penyedia data candle historis dan timeframe yang didukung. Snapshot harga saat ini saja tidak cukup untuk menghitung indikator berbasis rangkaian candle.
- Tambahkan grafik candle serta pilihan timeframe hanya setelah data historis tersedia dan valid.
- Implementasikan indikator yang transparan (misalnya SMA/EMA dan RSI), dengan parameter yang terlihat dan pengujian untuk perhitungan.
- Jika data historis atau indikator tidak tersedia, tampilkan status tidak tersedia; jangan membuat nilai atau sinyal sintetis.

**Selesai jika:** perhitungan indikator dapat diuji dengan data contoh yang diketahui, timeframe dan sumber data ditampilkan, serta kondisi data tidak memadai dijelaskan.

### 3. Ringkasan sinyal yang dapat dijelaskan
- Pisahkan data pasar, perhitungan indikator, dan presentasi UI agar masing-masing dapat diuji.
- Tampilkan alasan, indikator yang digunakan, timeframe, waktu pembuatan, serta status data pada setiap ringkasan.
- Jangan menyebut skor sebagai probabilitas atau jaminan keuntungan kecuali metodologi dan validasinya memang mendukung klaim tersebut.
- Sertakan penafian bahwa analisis bukan rekomendasi finansial.

**Selesai jika:** setiap ringkasan dapat ditelusuri ke data dan aturan yang membentuknya serta tidak mengarang input yang hilang.

### 4. Riwayat dan preferensi pengguna
- Ganti TODO pada `SignalRepository` dengan penyimpanan lokal setelah bentuk data dan kebutuhan retensi ditentukan.
- Simpan waktu, simbol, timeframe, sumber data, nilai indikator, alasan ringkasan, dan status kesegaran yang relevan.
- Tambahkan pengaturan preferensi (misalnya timeframe dan interval refresh) dengan nilai default yang aman.
- Sediakan cara untuk menghapus riwayat dan preferensi lokal.

**Selesai jika:** data tetap tersedia setelah aplikasi dimulai ulang, migrasi skema ditangani, dan penyimpanan dapat diuji.

### 5. Peringatan dan kualitas rilis
- Tambahkan peringatan harga/indikator hanya setelah sumber data, perilaku saat aplikasi tertutup, izin Android, dan batasan pengiriman notifikasi ditentukan.
- Uji aksesibilitas, berbagai ukuran layar, zona waktu, dan format angka.
- Tambahkan pemeriksaan build dan test otomatis pada CI, serta dokumentasikan cara menjalankan test.
- Tinjau izin aplikasi dan minimalkan data pengguna yang dikumpulkan.

**Selesai jika:** build dan test berjalan otomatis, perilaku notifikasi dijelaskan, dan rilis tidak memerlukan izin yang tidak relevan.

## Urutan implementasi yang disarankan

1. Tambahkan test parsing dan status kesegaran untuk koneksi BiQuote yang sudah ada.
2. Evaluasi ketersediaan serta ketentuan penyedia candle historis sebelum merancang grafik/indikator.
3. Rancang model domain dan komponen analisis yang terpisah dari Activity.
4. Implementasikan penyimpanan riwayat setelah model sinyal dan retensi disepakati.
5. Lanjutkan ke peringatan, penyempurnaan UI, dan otomasi rilis.

## Batasan dan keputusan terbuka

- Ketersediaan, latensi, ketentuan penggunaan, dan batas permintaan penyedia data perlu diverifikasi sebelum dipakai untuk fitur baru.
- Jangan menyimpan kredensial API di repository atau aplikasi. Jika penyedia memerlukan kunci rahasia, gunakan arsitektur backend yang sesuai.
- Perlu diputuskan timeframe, metode indikator, periode penyimpanan riwayat, serta apakah aplikasi hanya untuk pemantauan atau juga memerlukan notifikasi.
- Tidak ada strategi yang menjamin keuntungan; validasi historis pun tidak menjamin kinerja di masa depan.
