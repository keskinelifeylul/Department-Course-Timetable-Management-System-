# Sürüm 3: Final Release (Week 11–13 / Checkpoint 3) — Tamamlanmış Proje

Bu klasör, PDF Bölüm 8.3 (**Checkpoint 3 — Final**) ve Bölüm 12'de istenen **tüm özellikleri** eksiksiz içerir:
- `data/`: Örnek veri dosyaları (`courses.csv`, `instructors.txt`, `rooms.csv`, `constraints.txt`).
- `src/v3/`:
  - Tüm zorunlu (`HC-01`..`HC-09`) ve esnek (`SC-01`..`SC-04`) kısıtları denetleyen otomatik ders programı algoritması (**Screen 1–5**),
  - Canlı kısıt kontrolü yapan manuel ders yeri/saati değiştirme ve silme modülü (**Screen 6**),
  - Kapasitesi yetmeyen (`NO ROOM`) dersler için uygun boş saat bularak Öğrenci İşleri'ne resmi sınıf talep e-postası üreten ve kaydeden modül (**Screen 7**),
  - Programı `output/timetable.txt` ve `output/timetable.csv` olarak dışa aktarma (**Seçenek 8**).
- `docs/`:
  - `requirements/SRS-final.md`
  - `design/SDD-final.md` (UML Sınıf Diyagramı + Algoritma Akış Şeması)
  - `testing/Test-Results.md` (14 Senaryoluk Manuel Test Tablosu)

## Çalıştırma
[`src/v3/Main.java`](file:///c:/Users/keski/OneDrive/Masaüstü/dd/v3-final/src/v3/Main.java) dosyasını açıp **`Run`** butonuna tıklayabilir veya terminalden şu komutları çalıştırabilirsiniz:
```powershell
# İnteraktif menü:
java -cp bin v3.Main

# Otomatik demo:
java -cp bin v3.Main --demo
```

