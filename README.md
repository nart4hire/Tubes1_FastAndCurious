# Fast And Curious
Pemanfaatan Algoritma Greedy dalam Aplikasi Permainan "Overdrive"
By:
- 13520111/Rizky Akbar Asmaran
- 13520129/Nathanael Santoso
- 13520147/Aloysius Gilang Pramudya

## Spesifikasi Pengumpulan
Program disimpan dalam folder Tubes1_NamaKelompok dengan NamaKelompok merupakan nama kelompok yang terdaftar pada https://bit.ly/KelompokStima12022. Berikut merupakan struktur dari isi folder tersebut.
- Folder src berisi source code (Folder bot kalian dengan struktur seperti folder
java di dalam “starter-bots”)
- Folder bin berisi executable (File .jar) dengan format nama_kelompok.jar
- Folder doc berisi laporan tugas besar dengan format nama_kelompok.pdf
- README untuk tata cara penggunaan yang minimal berisi:
  - Penjelasan singkat algoritma greedy yang diimplementasikan
  - Requirement program dan instalasi tertentu bila ada
  - Command atau langkah-langkah dalam meng-compile atau build program
  - Author / identitas pembuat
- BONUS:
  - Setiap kelompok membuat video aplikasi yang mereka buat kemudian mengunggahnya ke YouTube. Video yang dibuat harus memiliki audio dan
menampilkan wajah dari setiap anggota kelompok. Pada waktu demo aplikasi di depan asisten, mahasiswa mengakses video YouTube tersebut dan memutarnya di depan asisten. Beberapa contoh video tubes tahun-tahun sebelumnya dapat dilihat di YouTube dengan menggunakan kata kunci “Tubes Stima”, “Tugas besar stima”, “strategi algoritma”, dll.

## Spesifikasi Keperluan
1. Starter sudah terdapat di folder src/Original Files
2. Untuk menjalankan permainan, kalian butuh beberapa requirement dasar sebagai berikut.
  - Java (minimal Java 8): https://www.oracle.com/java/technologies/downloads/#java8
  - IntelIiJ IDEA: https://www.jetbrains.com/idea/
  - NodeJS: https://nodejs.org/en/download/
3. Untuk menjalankan permainan, kalian dapat membuka file “run.bat” (Untuk Windows dapat buka dengan double-click, Untuk Linux/Mac dapat menjalankan command “make run”).
4. Secara default, permainan akan dilakukan diantara reference bot (default-nya berbahasa Java) dan starter bot (default-nya berbahasa JavaScript) yang disediakan. Untuk mengubah hal tersebut, silahkan edit file “game-runner-config.json”. Anda juga dapat mengubah file “bot.json” dalam direktori “starter-bots” untuk mengatur informasi terkait bot anda.
5. Silahkan bersenang-senang dengan memodifikasi bot yang disediakan di starter-bots. Ingat bahwa bot kalian harus menggunakan bahasa Java dan di-build menggunakan IntelIiJ sebelum menjalankan permainan kembali. Dilarang menggunakan kode program yang sudah ada untuk pemainnya atau kode program lain yang diunduh dari Internet. Mahasiswa harus membuat program sendiri, tetapi belajar dari program yang
sudah ada tidak dilarang.
6. (Optional) Anda dapat melihat hasil permainan dengan menggunakan visualizer berikut https://github.com/Affuta/overdrive-round-runner
7. Untuk referensi lebih lanjut, silahkan eksplorasi di https://github.com/EntelectChallenge/2020-Overdrive.

## Spesifikasi Permainan
1. Peta permainan memiliki bentuk array 2 dimensi yang memiliki 4 jalur lurus. Setiap jalur dibentuk oleh block yang saling berurutan, panjang peta terdiri atas 1500 block. Terdapat 5 tipe block, yaitu Empty, Mud, Oil Spill, Flimsy Wall, dan Finish Line yang masing-masing karakteristik dan efek berbeda. Block dapat memuat powerups yang bisa diambil oleh mobil yang melewati block tersebut.
2. Beberapa powerups yang tersedia adalah:
  - Oil item, dapat menumpahkan oli di bawah mobil anda berada.
  - Boost, dapat mempercepat kecepatan mobil anda secara drastis.
  - Lizard, berguna untuk menghindari lizard yang mengganggu jalan mobil anda.
  - Tweet, dapat menjatuhkan truk di block spesifik yang anda inginkan.
  - EMP, dapat menembakkan EMP ke depan jalur dari mobil anda dan membuat mobil musuh (jika sedang dalam 1 lane yang sama) akan terus berada di lane yang sama sampai akhir pertandingan. Kecepatan mobil musuh juga dikurangi 3.
3. Bot mobil akan memiliki kecepatan awal sebesar 5 dan akan maju sebanyak 5 block untuk setiap round. Game state akan memberikan jarak pandang hingga 20 block di depan dan 5 block di belakang bot sehingga setiap bot dapat mengetahui kondisi peta permainan pada jarak pandang tersebut.
4. Terdapat command yang memungkinkan bot mobil untuk mengubah jalur, mempercepat, memperlambat, serta menggunakan powerups. Pada setiap round, masing-masing pemain dapat memberikan satu buah command untuk mobil mereka. Berikut jenis-jenis command yang ada pada permainan:
  - NOTHING
  - ACCELERATE
  - DECELERATE
  - TURN_LEFT
  - TURN_RIGHT
  - USE_BOOST
  - USE_OIL
  - USE_LIZARD
  - USE_TWEET \[lane\] \[block\]
  - USE_EMP
  - FIX
5. Command dari kedua pemain akan dieksekusi secara bersamaan (bukan sekuensial) dan akan divalidasi terlebih dahulu. Jika command tidak valid, bot mobil tidak akan melakukan apa-apa dan akan mendapatkan pengurangan skor.
6. Bot pemain yang pertama kali mencapai garis finish akan memenangkan pertandingan. Jika kedua bot mencapai garis finish secara bersamaan, bot yang akan memenangkan pertandingan adalah yang memiliki kecepatan tercepat, dan jika kecepatannya sama, bot yang memenangkan pertandingan adalah yang memiliki skor terbesar.

## Spesifikasi Laporan

- Cover: Cover laporan ada foto anggota kelompok (foto bertiga). Foto ini menggantikan
logo “gajah” ganesha.
- Bab 1: Deskripsi tugas (dapat menyalin spesifikasi tugas ini).
- Bab 2: Landasan Teori.
  - Dasar teori (algoritma greedy) secara umum
  - Bagaimana cara kerja program secara umum (bagaimana bot melakukan
aksinya, bagaimana mengimplementasikan algoritma greedy ke dalam bot,
bagaimana menjalankan game engine, dll).
- Bab 3: Aplikasi strategi greedy.
  - Proses mapping persoalan Overdrive menjadi elemen-elemen algoritma Greedy
(himpunan kandidat, himpunan solusi, fungsi solusi, fungsi seleksi, fungsi
kelayakan, fungsi objektif)
  - Eksplorasi alternatif solusi greedy yang mungkin dipilih dalam persoalan
Overdrive
  - Analisis efisiensi dari kumpulan alternatif solusi greedy yang dirumuskan
  - Analisis efektivitas dari kumpulan alternatif solusi greedy yang dirumuskan
  - Strategi greedy yang dipilih (yang akan diimplementasikan dalam program)
beserta alasan dan pertimbangan pemilihan strategi tersebut.
- Bab 4: Implementasi dan pengujian.
  - Implementasi algoritma greedy pada program bot dalam game engine yang
digunakan (pseudocode yang cukup detail dengan komentar untuk pembaca
kode agar mudah dipahami).
  - Penjelasan struktur data yang digunakan dalam program bot Overdrive dan
struktur data tambahan jika ada.
  - Analisis dari desain solusi algoritma greedy yang diimplementasikan pada setiap
pengujian yang dilakukan. Misalnya adalah apakah strategi greedy berhasil
mendapatkan nilai optimal, lalu jika tidak, dalam kondisi seperti apa strategi
greedy tidak berhasil mendapatkan nilai optimal, dsb.
- Bab 5: Kesimpulan dan saran.
- Daftar Pustaka