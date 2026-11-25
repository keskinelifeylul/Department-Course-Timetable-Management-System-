# Checkpoint 2 Report (V2 — Code v3 + Code v4 Core Scheduling)

This report documents the Checkpoint 2 (`V2`) milestone of the Department Course Timetable Management System, covering core automatic scheduling (`Code v3`) and timetable views/reporting (`Code v4`).

## Team Information

| Field | Value |
| :--- | :--- |
| **Team** | Arda Güneş · Elif Eylül Keskin |
| **Member 1** | Arda Güneş · `ardagunes1907` |
| **Member 2** | Elif Eylül Keskin · `keskinelifeylul` |
| **Repository** | https://github.com/keskinelifeylul/Department-Course-Timetable-Management-System- |
| **Milestone** | `V2` (`Code v2` input loading + `Code v3` initial scheduling & `HC-01`–`HC-06` + `Code v4` options `1–5`) |
| **Documents shown** | SRS v2, SDD v2 |

---

## 1. Completed Scope in V2

| Component | Implemented in V2 | Remaining for V3 (Final) |
| :--- | :--- | :--- |
| **Input loading (`Code v2`)** | `DataLoader.loadAll()`, `Course.createSessions()`, Menu Option 1 (`FR-01`–`FR-03`, `FR-09`–`FR-13`) | — |
| **Constraint checking (`Code v3`)** | `Room.isSuitableFor()`, `ConstraintChecker.checkHard()` with `HC-01` (instructor conflict), `HC-02` (room conflict), `HC-03` (year-group conflict), `HC-04` (working hours), `HC-05` (room capacity), `HC-06` (room type) | `HC-07` (instructor availability), `HC-08` (fixed-time/specific-room enforcement), `HC-09` (room availability), `checkSoft()` (`SC-01`–`SC-03`), `Instructor.prefers()` |
| **Automatic scheduling (`Code v3` & `Code v4`)** | `Scheduler.generateTimetable()`, `sortSessions()`, `scheduleSession()`, `findRoom()`, `hasSuitableRoom()`, Menu Option 2 (`FR-04`, `FR-05`, `FR-15`, `FR-16`, `FR-18`, `FR-25`); mandatory `FIXED_TIME`/`SPECIFIC_ROOM` sessions are safely marked as planned for `V3` | Full `scheduleFixedSessions()` and `SPECIFIC_ROOM` placement (`FR-14`, `HC-08`) |
| **Timetable views (`Code v4`)** | `ReportGenerator.printYearTimetable()` (Option 3, 5×8 grid), `printAllSessions()` (Option 4), `printUnscheduled()` (Option 5) (`FR-06`, `FR-19`, `FR-20`) | Manual modification/removal (Option 6, `FR-21`–`FR-23`), classroom request email draft (Option 7, `FR-07`, `FR-24`, `FR-26`), saving timetable to `output/timetable.txt` (Option 8, `FR-08`) |

---

## 2. Sample Run Output (V2)

Command:
```powershell
javac -d out src/*.java
java -cp out Main
```

Output for Options `1`, `2`, `3` (Year 2), `4`, `5`:
```text
=== LOAD INPUT DATA ===
Reading data/courses.csv .... 6 courses loaded
Reading data/instructors.txt .... 5 instructors loaded
Reading data/rooms.csv .... 4 rooms loaded
Reading data/constraints.txt .... 5 requests loaded (4 mandatory, 1 preferred)

WARNINGS:
  courses.csv, line 8: invalid value "three" for theory hours -> record skipped
  constraints.txt, line 7: course "SENG499" not found -> request ignored

Data loaded with 2 warning(s). 10 session(s) created.

=== AUTOMATIC TIMETABLE GENERATION ===
  [FAIL] SENG301-T  HC-08: mandatory FIXED_TIME request (Wed 13:00-16:00) planned for V3
  [FAIL] SENG305-T  HC-08: mandatory FIXED_TIME request (Fri 09:00-12:00) planned for V3
  [OK]   SENG101-T  -> Mon 09:00-12:00, Room A101
  [FAIL] SENG101-L  HC-05: no laboratory with capacity >= 70
  [OK]   SENG201-T  -> Mon 12:00-15:00, Room A101 (after 3 rejected attempts)
  [FAIL] SENG201-L  HC-05: no laboratory with capacity >= 55
  [OK]   SENG203-T  -> Tue 09:00-12:00, Room A102 (after 12 rejected attempts)
  [OK]   SENG203-L  -> Mon 15:00-17:00, Room B202 (after 6 rejected attempts)
  [OK]   SENG401-T  -> Mon 09:00-11:00, Room A102
  [FAIL] SENG401-L  HC-08: mandatory SPECIFIC_ROOM request (B202) planned for V3
--------------------------------------------------
SUMMARY: Total sessions: 10 | Scheduled: 5 | Unscheduled: 5 | Warnings: 0
--------------------------------------------------

=== WEEKLY TIMETABLE - YEAR 2 ===
+-------------+----------------+----------------+----------------+----------------+----------------+
| Time        | Monday         | Tuesday        | Wednesday      | Thursday       | Friday         |
+-------------+----------------+----------------+----------------+----------------+----------------+
| 09:00-10:00 |                | SENG203-T A102 |                |                |                |
| 10:00-11:00 |                | SENG203-T A102 |                |                |                |
| 11:00-12:00 |                | SENG203-T A102 |                |                |                |
| 12:00-13:00 | SENG201-T A101 |                |                |                |                |
| 13:00-14:00 | SENG201-T A101 |                |                |                |                |
| 14:00-15:00 | SENG201-T A101 |                |                |                |                |
| 15:00-16:00 | SENG203-L B202 |                |                |                |                |
| 16:00-17:00 | SENG203-L B202 |                |                |                |                |
+-------------+----------------+----------------+----------------+----------------+----------------+
T = Theory, L = Laboratory

Session details:
  SENG201-T  Object-Oriented Programming          Dr. Demir      Mon 12:00-15:00  A101
  SENG203-T  Data Structures                      Dr. Kaya       Tue 09:00-12:00  A102
  SENG203-L  Data Structures                      Dr. Kaya       Mon 15:00-17:00  B202

Unscheduled sessions for Year 2:
  SENG201-L  (2 h, 55 students) [NO ROOM] HC-05: no laboratory with capacity >= 55

=== ALL SCHEDULED SESSIONS ===
Session    Course Title                         Year  Instructor     Day & Time       Room  
--------------------------------------------------------------------------------------------
SENG101-T  Introduction to Software Engineering 1     Dr. Kaya       Mon 09:00-12:00  A101  
SENG201-T  Object-Oriented Programming          2     Dr. Demir      Mon 12:00-15:00  A101  
SENG203-T  Data Structures                      2     Dr. Kaya       Tue 09:00-12:00  A102  
SENG203-L  Data Structures                      2     Dr. Kaya       Mon 15:00-17:00  B202  
SENG401-T  Software Project Management          4     Dr. Aksoy      Mon 09:00-11:00  A102  

=== UNSCHEDULED SESSIONS ===
#   Session    Year  Hours  Students  Problem type  Reason
--------------------------------------------------------------------------------------------
1   SENG101-L  1     2      70        NO ROOM       HC-05: no laboratory with capacity >= 70
2   SENG201-L  2     2      55        NO ROOM       HC-05: no laboratory with capacity >= 55
3   SENG301-T  3     3      45        CONSTRAINT    HC-08: mandatory FIXED_TIME request (Wed 13:00-16:00) planned for V3
4   SENG305-T  3     3      40        CONSTRAINT    HC-08: mandatory FIXED_TIME request (Fri 09:00-12:00) planned for V3
5   SENG401-L  4     2      30        CONSTRAINT    HC-08: mandatory SPECIFIC_ROOM request (B202) planned for V3

-> Sessions marked NO ROOM require a classroom request email (option 7, planned for V3).
-> Sessions marked CONSTRAINT must be resolved by the coordinator.

=== SOFT-CONSTRAINT WARNINGS ===
No soft-constraint warnings.
```
