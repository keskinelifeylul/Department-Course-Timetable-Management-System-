# V2 — Department Course Timetable Management System (Checkpoint 2: Code v3 + Code v4 Core Scheduling)

## Kapsam (V2)
- **Belge karşılığı:** Checkpoint 2 (`Code v3` başlangıç zamanlama algoritması ve `HC-01`–`HC-06` kısıt denetimi + `Code v4` çalışan çekirdek zamanlama ve menü seçenekleri `1–5`).
- **Karşılanan gereksinimler:**
  - **Veri yükleme ve doğrulama (`Code v2`):** `FR-01`, `FR-02`, `FR-03`, `FR-09`, `FR-10`, `FR-11`, `FR-12`, `FR-13` (`DataLoader`, `Course.createSessions()`, Seçenek 1 / Screen 2).
  - **Otomatik zamanlama ve çakışma denetimi (`Code v3` & `Code v4`):** `FR-04`, `FR-05`, `FR-15`, `FR-16`, `FR-18`, `FR-25`, `HC-01`–`HC-06`, `Room.isSuitableFor()`, `TimeSlot.isWithinWorkingHours()` (`Scheduler`, `ConstraintChecker`, Seçenek 2 / Screen 3).
  - **Program görünümleri ve raporlama (`Code v4`):** `FR-06` (Seçenek 3 / Screen 4 — dönem bazlı 5×8 haftalık tablo), `FR-19` (Seçenek 4 — yerleşen tüm oturumlar), `FR-20` (Seçenek 5 / Screen 5 — yerleşemeyen oturumlar ve uyarılar).
  - **Fonksiyonel olmayan gereksinimler:** `NFR-01`, `NFR-02`, `NFR-03`, `NFR-04`, `NFR-05`.
- **Kapsam dışı (`V3` / Final sürümüne bırakılan — `Code v5` & `Code v6`):**
  - Zorunlu sabit saat / özel oda taleplerinin yerleştirilmesi ve müsaitlik kısıtları (`FR-14`, `HC-07`, `HC-08`, `HC-09`), esnek kısıtlar (`FR-17`, `SC-01`–`SC-03`, `Instructor.prefers()`), manuel değişiklik/silme (`FR-21`–`FR-23`, Seçenek 6), derslik talep e-postası ve dosyaya kaydetme (`FR-07`, `FR-08`, `FR-24`, `FR-26`, Seçenek 7 ve 8).
- **Dokümanlar (`docs/`):**
  - `docs/Checkpoint-1-Report.md`: Checkpoint 1 tasarım–kod tutarlılık raporu.
  - `docs/Checkpoint-2-Report.md`: Checkpoint 2 (V2) ilerleme, tasarım–kod tutarlılık ve çalışma çıktısı raporu.
  - `docs/requirements/SRS-v2.md`: Yazılım Gereksinim Belirtimi (SRS v2).
  - `docs/design/SDD-v2.md`: Yazılım Tasarım Belgesi (SDD v2) ve gömülü UML / akış diyagramları (`class_diagram.dot`, `class_diagram.png`, `flowchart.dot`, `flowchart.png`).

## Derleme ve Çalıştırma
Proje kök klasörü içinden (JDK 21+):
```powershell
javac -d out src/*.java
java -cp out Main
```
Farklı bir veri klasörü belirtmek için:
```powershell
java -cp out Main path/to/data
```
