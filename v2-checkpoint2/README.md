# Sürüm 2: Checkpoint 2 (Week 8–10) — Core Implementation & Automatic Scheduling

Bu klasör, PDF Bölüm 8.2 (**Checkpoint 2 — Week 10**) gereksinimlerine uygun olarak:
- Dış veri dosyalarının (`.csv` ve `.txt`) okunması ve hatalı kayıt uyarılarının gösterilmesi (**Screen 2**),
- Sabit saatli (`FIXED_TIME`) ve normal ders oturumlarının otomatik yerleştirilmesi (**Screen 3**),
- Zorunlu kısıtların (`HC-01` Hoca Çakışması, `HC-02` Oda Çakışması, `HC-03` Dönem Çakışması, `HC-04` Mesai Saatleri, `HC-05` Oda Kapasitesi, `HC-06` Oda Tipi, `HC-07` Hoca Müsaitliği) denetlenmesi,
- Dönem bazlı (Year 1–4) haftalık ders programı tablosunun gösterilmesi (**Screen 4**),
- Atanamayan derslerin (`UNSCHEDULED SESSIONS`) nedenleriyle raporlanması (**Screen 5**)
özelliklerini içerir. *(Seçenek 6 Manuel Değişiklik ve Seçenek 7 Sınıf Talep E-postası PDF Bölüm 7 planına uygun olarak 11–12. hafta Final sürümüne bırakılmıştır.)*

## Çalıştırma
[`src/v2/Main.java`](file:///c:/Users/keski/OneDrive/Masaüstü/dd/v2-checkpoint2/src/v2/Main.java) dosyasını açıp **`Run`** butonuna tıklayabilir veya terminalden şu komutu çalıştırabilirsiniz:
```powershell
java -cp bin v2.Main
```

