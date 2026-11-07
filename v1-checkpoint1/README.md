# Sürüm 1: Checkpoint 1 (Week 6–7) — Code v1 (Java Project Skeleton)

Bu klasör, PDF Bölüm 6.3 (**Design–Code Consistency**) ve Bölüm 8.1 (**Checkpoint 1**) gereksinimlerine uygun **Code v1 (İskelet Kod)** ve tasarım dokümanlarını içerir.

## Klasör Yapısı
- `src/v1/`:
  - `Main.java`: Ana menüyü (Screen 1) çalıştırır.
  - `Course.java`: Ders sınıfı (nitelikler, constructor, getter/setter).
  - `Instructor.java`: Öğretim elemanı sınıfı ve `isAvailable` metodu.
  - `Room.java`: Derslik/Laboratuvar sınıfı, kapasite ve tip kontrol metotları.
  - `TimeSlot.java`: Gün ve saat aralığı sınıfı, mesai ve çakışma metotları.
  - `Timetable.java`: Program görüntüleme ve kaydetme metot iskeletleri.
  - `Scheduler.java`: Veri yükleme, otomatik atama ve kısıt kontrol metot iskeletleri.
- `docs/`:
  - `design/SDD-v2.md`: UML Sınıf Diyagramı ve Algoritma Akış Şeması.

## Çalıştırma
[`src/v1/Main.java`](file:///c:/Users/keski/OneDrive/Masaüstü/dd/v1-checkpoint1/src/v1/Main.java) dosyasını açıp **`Run`** butonuna tıklayabilir veya terminalden şu komutu çalıştırabilirsiniz:
```powershell
java -cp bin v1.Main
```

