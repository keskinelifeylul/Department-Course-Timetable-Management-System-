# V1 — Department Course Timetable Management System (Checkpoint 1 Skeleton + Code v2 Data Loading)

## Kapsam (V1)
- **Belge karşılığı:** Checkpoint 1 (SDD v2 17 tür iskeleti, menü) + Code v2 (`DataLoader` ve `Course.createSessions()`)
- **Karşılanan gereksinimler:** `FR-01`, `FR-02`, `FR-03`, `FR-09`, `FR-10`, `FR-11`, `FR-12`, `FR-13`, `NFR-01`, `NFR-02`, `NFR-04`, `NFR-05` (girdi hata/uyarı mesajları), `TimeSlot.isWithinWorkingHours()`, `Instructor.isAvailable()`, `Room.isAvailable()`
- **Kapsam dışı (V2/V3'e bırakılan):** Otomatik zamanlama (`FR-04`, `FR-05`, `FR-14`–`FR-18`, `FR-25`, `HC-01`–`HC-09`, `SC-01`–`SC-03`), program görünümleri (`FR-06`, `FR-19`, `FR-20`), manuel değişiklik (`FR-21`–`FR-23`), derslik talebi ve dosya kaydetme (`FR-07`, `FR-08`, `FR-24`, `FR-26`).
- **Dokümanlar (`docs/`):**
  - `docs/Checkpoint-1-Report.md`: Checkpoint 1 tasarım–kod tutarlılık raporu.
  - `docs/requirements/SRS-v2.md`: Yazılım Gereksinim Belirtimi (SRS v2).
  - `docs/design/SDD-v2.md`: Yazılım Tasarım Belgesi (SDD v2) ve gömülü UML / akış diyagramları (`class_diagram.dot`, `class_diagram.png`, `flowchart.dot`, `flowchart.png`).

## Derleme ve Çalıştırma
`V1/` klasörü içinden (JDK 21+):
```powershell
javac -d out src/*.java
java -cp out Main
```
Farklı bir veri klasörü belirtmek için:
```powershell
java -cp out Main path/to/data
```
