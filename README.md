# Fast And Curious

Pemanfaatan Algoritma Greedy dalam Aplikasi Permainan "Overdrive"
By:
- 13520111/Rizky Akbar Asmaran
- 13520129/Nathanael Santoso
- 13520147/Aloysius Gilang Pramudya

## Introduksi

Repository ini adalah penerapan algoritma greedy sebagai engine bot permainan overdrive yang dikembangkan oleh entelect untuk turnamen koding mereka pada tahun 2020. Algoritma greedy yang diterapkan mengikuti beberapa langkah berikut:

1. Mencari semua command yang mungkin dilakukan pada state tertentu
2. Memasukkan command tersebut ke dalam list berisi himpunan kandidat
3. Menilai setiap command berdasarkan fungsi objektif:
  - f(x) = (SpeedStateAkhir - SpeedStateAwal + BobotSpeedState, DamageAkhir - DamageAwal + BobotDamage), x = command
  - Speed State dan Damage Dihitung berdasarkan peraturan game dan pembobotan berdasarkan tabel pembobotan yang terdapat pada laporan.
4. Menyimpan command pertama pada variabel prediksi terbaik
5. Melakukan iterasi terhadap anggota list berdasarkan fungsi seleksi:
  - Jika menemukan command yang pertambahan speed state lebih besar, menggantikan prediksi terbaik dengan command yang baru ditemukan.
  - Jika menemukan command yang pertambahan speed state sama dengan prediksi, menggantikan prediksi terbaik dengan command yang baru ditemukan jika pengurangan damage lebih besar.
6. Menjalankan command terbaik

## Cara compile

Prerequisite:
- Java Versi Terbaru
- Maven Versi Terbaru
- IntelliJ IDEA Versi Terbaru

Cara:
- Pada directory repository lakukan "cd src\FastAndCurious"
- Buatlah project IDEA pada current directory
- Pada IDEA -> view | Tool Windows | Maven
- Pada folder lifecycles ada pilihan "package", select lalu klik tombol run hijau pada tool window

## Struktur Repository

|-- Repository
  |-- bin
    |-- target
      |-- ... ( class folders and maven build files )
      \- FastAndCurious-jar-with-dependencies.jar ( Diperlukan jika run memakai game-runner-with-dependencies )
      \- FastAndCurious.jar
    \- bot.json
  |-- doc
    \- FastAndCurious.pdf
  |-- src
    |-- FastAndCurious
      |-- src
        |-- META-INF ( Untuk membantu build )
        |-- za\co\entelect\challenge
          |-- command
            \- ... ( Primitif yang diperoleh dari starter bot dan dimodifikasi )
          |-- entities
            \- ... ( Primitif yang diperoleh dari starter bot dan dimodifikasi )
          |-- enums
            \- ... ( Primitif yang diperoleh dari starter bot dan dimodifikasi )
          \- Bot.java
          \- Main.java ( Bot runner yang diperoleh dari starter bot dan dimodifikasi )
        \- pom.xml ( Maven Compiler Settings )
    |-- Original Files
      \- starter-pack.zip ( zip original yang diperoleh dari website entelect )
    |-- starter-pack
      |-- reference-bot
        \- ... ( bot referensi awal )
      |-- starter-bots
        \- ... ( bot starter )
      \- game-config.json
      \- game-engine.jar
      \- game-runner-config.json ( konfigurasi game runner )
      \- game-runner-jar-with-dependencies.jar
      \- makefile ( untuk run memakai command make run )
      \- run.bat ( untuk run memakai command run.bat )
  \- LICENSE
  \- README.md

## Cara melakukan run game

Menggunakan repository ini:
- Pindahkan working directory ke src\starter-pack ( Jika sudah berada di folder repository lakukan "cd src\starter-pack" )
- Ketik command "run.bat" atau "make run" dalam terminal
- Tonton hasil pertandingan antara bot

Menggunakan repository lain:
- Pindahkan folder bin ke directory yang diinginkan
- Ubah "game-runner-config.json" pada game engine yang dipakai sehingga data "player-a" atau "player-b" menunjuk pada folder bin yang dipindahkan sebelumnya ( contoh: '"player-a": "../../bin"' untuk repository ini )
- Ketik command "run.bat" atau "make run" dalam terminal
- Tonton hasil pertandingan antara bot

