# Val0x07

**Val0x07** adalah addon Fabric **client-side** untuk Simple Voice Chat (SVC). Mod ini mengganti frame PCM mikrofon keluar melalui `ClientSoundEvent`, yaitu event API resmi SVC yang dipanggil sebelum audio dikodekan dan dikirim ke server. Tidak ada mixin, refleksi, atau akses ke kelas internal SVC.

> Mod ini memengaruhi **audio mikrofon yang Anda kirim**, bukan audio yang Anda dengar. Server hanya perlu menjalankan Simple Voice Chat normal; Val0x07 tidak perlu dan tidak boleh dipasang di server.

## Kompatibilitas dan dependensi

| Komponen | Versi proyek |
|---|---:|
| Minecraft Java Edition | 1.21.11 |
| Java | 21 |
| Fabric Loader | 0.19.3 |
| Fabric API | 0.141.6+1.21.11 |
| Simple Voice Chat | fabric-1.21.11-2.6.22 |
| Simple Voice Chat API | 2.6.20 |

Simple Voice Chat adalah **dependensi wajib saat runtime**, tetapi tidak dibundel dalam JAR Val0x07. Pasang SVC versi Fabric yang kompatibel secara terpisah di folder `mods` instance klien Anda.

## Build

Pastikan Java Development Kit (JDK) 21 tersedia. Dari root proyek, jalankan:

```bash
./gradlew build
```

Artefak hasil berada di `build/libs/val0x07-1.0.0.jar`. Berkas dengan classifier `-sources` hanya berisi sumber. Jangan memasang kedua JAR sekaligus.

## Instalasi dan pengujian

1. Siapkan instance Minecraft **1.21.11** dengan Fabric Loader, Fabric API, dan Simple Voice Chat versi Fabric.
2. Salin `build/libs/val0x07-1.0.0.jar` ke folder `mods` instance klien yang sama.
3. Jalankan Minecraft lalu hubungkan ke server atau dunia lokal yang telah mengaktifkan Simple Voice Chat.
4. Buka menu Controls Minecraft dan cari kategori **Val0x07**. Keybind `Open Config` secara default adalah `Right Shift`; `Toggle Effects` sengaja tidak terikat secara default.
5. Pilih preset, lakukan voice test atau berbicara dengan pemain lain, lalu sesuaikan preset kustom jika diperlukan.

Pengaturan disimpan lokal pada `config/val0x07.json`. File tersebut menyimpan preset aktif, status ON/OFF, dan semua preset kustom. Keybind dikelola oleh sistem options Minecraft seperti keybind mod Fabric lainnya.

## Preset bawaan

| Preset | Parameter awal | Catatan tuning |
|---|---|---|
| **War Radio** | Band-pass 300–3400 Hz; drive 2.2x; soft clip 0.82; noise 0.018; crackle 18 burst/menit selama 95 ms; pitch −0.65 semitone | Turunkan drive/noise bila suara terlalu kasar. Crackle dibuat jarang agar tidak menutupi ucapan. |
| **Female Voice** | Pitch +1.65 semitone; formant +0.28; tanpa filter, distorsi, noise, maupun crackle | Pergeseran dibuat halus agar sesuai asumsi suara input medium/tinggi. Efek algoritmik DSP tidak dapat menggantikan kualitas konversi AI/ML. |
| **Custom** | Seluruh parameter dapat diatur dan disimpan | Buat preset baru, sesuaikan slider, lalu tekan **Save**. Gunakan **Load** untuk membatalkan edit belum tersimpan dan **Delete** untuk menghapus preset. |

## Arsitektur audio

Pipeline bekerja pada PCM 16-bit mono 48 kHz. Modul `BandpassFilter`, `DistortionEffect`, `NoiseGenerator`, `CrackleGenerator`, `PitchShifter`, dan `FormantShifter` dapat dipakai ulang oleh semua preset. Pitch shifter memakai circular buffer, interpolasi linear, dan crossfade alih-alih FFT resolusi tinggi untuk menjaga beban CPU rendah pada perangkat seperti PojavLauncher. Formant shifter menggunakan pemetaan energi resonansi band-pass yang optimal untuk perubahan kecil; hindari mengatur slider secara ekstrem bila hasil natural menjadi prioritas.

## Lisensi

Lisensi proyek adalah **All Rights Reserved (ARR)**. Lihat [LICENSE](LICENSE). Tidak ada hak redistribusi atau penggunaan ulang yang diberikan tanpa izin tertulis dari author.
