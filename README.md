# 📚 StudyStyle — Aplikasi Tes Gaya Belajar Mahasiswa

![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)
![Java](https://img.shields.io/badge/Language-Java-orange?logo=java)
![API](https://img.shields.io/badge/Min%20SDK-24-blue)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

> **"Belajar sesuai gaya terbaikmu."**

**StudyStyle** adalah aplikasi Android yang membantu mahasiswa mengenali gaya belajar dominan mereka melalui kuis psikologi VAK (*Visual, Auditory, Kinesthetic*), menampilkan hasil analisis lengkap, serta memberikan rekomendasi metode belajar yang sesuai.

---

## ✨ Fitur Utama

| Fitur | Keterangan |
|---|---|
| 🔐 Login & Register | Autentikasi lokal dengan SQLite, validasi form lengkap |
| 📝 Tes Gaya Belajar | 15 pertanyaan VAK, dihitung secara otomatis |
| 📊 Hasil Analisis | Pie chart interaktif, persentase tiap gaya, kelebihan & kelemahan |
| 💬 Motivasi Harian | Quote dari API [quotable.io](https://quotable.io) secara real-time |
| 📜 Riwayat Tes | Semua hasil tes tersimpan di SQLite, tampil di RecyclerView |
| 👤 Profil User | Edit nama & jurusan, toggle dark mode, logout |
| 🌙 Dark / Light Mode | Tersimpan via SharedPreferences, diterapkan global |
| 📡 Offline Support | Data lokal tetap tampil saat tidak ada koneksi + tombol refresh |

---

## 🛠️ Spesifikasi Teknis

### Activity
- `SplashActivity` — Launcher utama, animasi logo, redirect otomatis
- `AuthActivity` — Menampung Login & Register via Fragment + TabLayout
- `MainActivity` — Menampung semua Fragment utama via Navigation Component

### Intent
- `SplashActivity → AuthActivity` (belum login)
- `SplashActivity → MainActivity` (sudah login)
- `AuthActivity → MainActivity` setelah login/register berhasil
- `ProfileFragment → AuthActivity` setelah logout

### Fragment & Navigation (Navigation Component)
- `HomeFragment` — Dashboard, greeting, quote API
- `TestFragment` — Kuis 15 pertanyaan dengan RecyclerView
- `ResultFragment` — Hasil tes, pie chart, riwayat
- `ProfileFragment` — Info user, dark mode, logout
- `LoginFragment` & `RegisterFragment` — Di dalam AuthActivity

### RecyclerView
- `QuestionAdapter` — Daftar pertanyaan kuis (dengan RadioGroup per item)
- `HistoryAdapter` — Daftar riwayat hasil tes

### Background Thread (Executor)
- `ExecutorManager` — Wrapper `ExecutorService` + `Handler` untuk operasi DB di background thread, hasilnya dikembalikan ke main thread

### Networking (Retrofit)
- API: [https://api.quotable.io/random](https://api.quotable.io/random)
- `ApiClient` — Singleton Retrofit dengan OkHttp logging interceptor
- `ApiService` — Interface endpoint Retrofit
- Tombol **"Coba Lagi"** muncul saat koneksi gagal

### Local Data Persistent
- **SQLite** via `DatabaseHelper` — Tabel `users` dan `results`
- **SharedPreferences** via `PreferenceManager` — Status login, data sesi user, preferensi dark mode, cache hasil tes terakhir
- Data tes tampil di `ResultFragment` meski offline (dari SharedPreferences & SQLite)

### Dark / Light Theme
- Dua file `themes.xml` (`values/` dan `values-night/`)
- Toggle via `SwitchMaterial` di ProfileFragment
- Diterapkan dengan `AppCompatDelegate.setDefaultNightMode()`

---

## 📁 Struktur Proyek

```
StudyStyle/
├── app/src/main/
│   ├── java/com/example/studystyle/
│   │   ├── activities/       # SplashActivity, AuthActivity, MainActivity
│   │   ├── fragments/        # Home, Test, Result, Profile, Login, Register
│   │   ├── adapters/         # QuestionAdapter, HistoryAdapter
│   │   ├── models/           # User, Question, Result, Quote
│   │   ├── api/              # ApiClient, ApiService, QuoteResponse
│   │   ├── database/         # DatabaseHelper (SQLite)
│   │   ├── utils/            # PreferenceManager, ThemeHelper, NetworkUtil, Constants
│   │   └── background/       # ExecutorManager, BackgroundTask
│   └── res/
│       ├── layout/           # Semua file XML layout
│       ├── drawable/         # Vector icons + shape drawables
│       ├── values/           # colors, strings, themes, dimens (light)
│       ├── values-night/     # themes.xml (dark)
│       ├── navigation/       # nav_graph.xml
│       ├── menu/             # bottom_nav_menu.xml
│       └── anim/             # fade_in, fade_out, slide_up
```

---

## 🚀 Cara Penggunaan

1. **Clone / download** repositori ini
2. Buka di **Android Studio** (minimal Electric Eel)
3. Sync Gradle (pastikan ada koneksi internet untuk download dependensi)
4. Jalankan di emulator atau perangkat fisik (min. Android 7.0 / API 24)
5. **Register** akun baru → isi nama, jurusan, email, password
6. Klik **Mulai Tes** → jawab 15 pertanyaan → **Lihat Hasil**
7. Lihat pie chart, deskripsi gaya belajar, dan riwayat tes

---

## 📦 Dependencies

```gradle
// Navigation Component
implementation 'androidx.navigation:navigation-fragment:2.7.7'
implementation 'androidx.navigation:navigation-ui:2.7.7'

// Retrofit + OkHttp
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

// MPAndroidChart (Pie Chart)
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'

// Material Components
implementation 'com.google.android.material:material:1.11.0'
```

---

## 🎨 Design

- **Color Palette**: Indigo (#4F46E5) + Orange accent (#F97316)
- **Gaya Belajar Colors**: Visual = Indigo · Auditori = Emerald · Kinestetik = Amber
- **Dark Mode**: Full support dengan `values-night/themes.xml`
- **Animasi**: Scale + fade pada splash, slide-up antar fragment, pie chart animated

---

## 👩‍💻 Dibuat untuk

**Tugas Final Lab Mobile 2026** — Tema: Pendidikan

---

## 📄 Lisensi

MIT License — bebas digunakan untuk keperluan pendidikan.
