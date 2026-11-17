# Rencana Pengembangan XAUUSD Analyzer Pro 2030

Dokumen ini mencatat urutan implementasi dan batasan aplikasi. Analisis adalah alat bantu edukasi, bukan nasihat atau jaminan hasil trading.

## Tahapan

1. **Keandalan data pasar:** parsing/status BiQuote, syarat `LIVE`, penanganan kegagalan dan unit test. Parsing freshness dan unit test kini tersedia pada branch fitur ini; build serta koneksi langsung masih perlu diverifikasi.
2. **Candle dan indikator:** candle OHLC dapat diagregasi dari quote yang benar-benar diterima, dengan SMA/EMA/RSI dan test. Ini adalah candle sampel in-memory, bukan data historis lengkap. Integrasi penyedia candle historis, grafik historis, timeframe dari provider, dan validasi ketentuan provider belum dilakukan.
3. **Ringkasan sinyal:** ringkasan teknikal menampilkan alasan, sumber, timeframe, waktu, nilai indikator, serta score sebagai indeks aturan (bukan probabilitas). Tidak ada hasil jika data belum memadai.
4. **Riwayat dan preferensi:** ringkasan disimpan privat di perangkat (maksimal 500), preferensi refresh/timeframe memiliki batas aman, dan riwayat dapat dihapus. Penyimpanan memakai SharedPreferences JSON dengan versi nama penyimpanan; belum memakai database/migrasi skema Room.
5. **Peringatan dan kualitas rilis:** alert kondisional, pengujian UI/aksesibilitas, CI, izin rilis, dan verifikasi perangkat masih perlu dikerjakan.

## Batasan dan keputusan terbuka

- Ketersediaan, ketentuan, latensi, timeframe, dan batas permintaan penyedia candle historis perlu diverifikasi sebelum grafik historis dibangun.
- Jangan menyimpan kredensial API di repository atau aplikasi. Jika penyedia memerlukan kunci rahasia, gunakan backend yang sesuai.
- Sampling quote berkala tidak sama dengan tick stream atau candle pasar lengkap; kehilangan polling berarti data kosong, bukan diisi dengan angka sintetis.
- Tidak ada strategi yang menjamin keuntungan; validasi historis pun tidak menjamin kinerja masa depan.
