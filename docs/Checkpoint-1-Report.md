# Checkpoint 1 Report

This report shows that our requirements, design and first Java code fit together. A fully working scheduling algorithm is not required at Checkpoint 1.

## Team Information

| Field | Value |
| :--- | :--- |
| **Team** | Arda Güneş · Elif Eylül Keskin |
| **Member 1** | Arda Güneş · `ardagunes1907` |
| **Member 2** | Elif Eylül Keskin · `keskinelifeylul` |
| **Repository** | https://github.com/keskinelifeylul/Department-Course-Timetable-Management-System- |
| **Code v1** | `V1/` (`code-v1` + `Code v2` input loading) |
| **Documents shown** | SRS v2, SDD v2 |
| **Date** | 10.11.2026 |

---

## 1. Submission Checklist

- [x] SRS v2 committed in `docs/requirements/`
- [x] SDD v2 committed in `docs/design/`
- [x] UML class diagram included in SDD v2 (source file `docs/design/class_diagram.dot` also committed)
- [x] Automatic scheduling algorithm flowchart included in SDD v2 (source file `docs/design/flowchart.dot` also committed)
- [x] Java project skeleton in `src/`, with the classes of the class diagram
- [x] Class diagram and Java classes are consistent (Section 2)
- [x] The application compiles and runs, and the main menu is displayed (Section 3)
- [x] `README.md` explains how to compile and run the application
- [x] Version structure and release documentation are maintained
- [x] Both members have contributed to requirements, design and implementation

---

## 2. Design–Code Consistency Matrix

| Class in diagram | Java file | Attributes match | Methods declared | Relationships implemented as | Difference and reason |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `Main` | `src/Main.java` | Yes | Yes | Local variable of type `ConsoleMenu` created in `main()` | None |
| `ConsoleMenu` | `src/ConsoleMenu.java` | Yes | Yes | Fields `loader: DataLoader`, `timetable: Timetable`, `scheduler: Scheduler`, `reports: ReportGenerator` | None |
| `DataLoader` | `src/DataLoader.java` | Yes | Yes | `HashMap<String, Course/Instructor/Room>` and `ArrayList<SpecialRequest>` fields | Diagram groups the five getters in two lines; all exist in the code. |
| `Course` | `src/Course.java` | Yes | Yes | Field `instructor: Instructor`; `ArrayList<Session> sessions` (composition, `1..2`) | Diagram shows getters in one line (`getCode() / getYear() / getStudents() …`); all getters exist in the code. |
| `Instructor` | `src/Instructor.java` | Yes | Yes | `ArrayList<Course> courses`; `ArrayList<TimeSlot> unavailableSlots` | `prefers()` returns `true` until V3 / Code v5 (body is a TODO); declaration matches. |
| `Room` | `src/Room.java` | Yes | Yes | `ArrayList<TimeSlot> unavailableSlots`; `type: RoomType` | `isSuitableFor()` returns `false` until V2 / Code v3 (body is a TODO); declaration matches. |
| `Session` | `src/Session.java` | Yes | Yes | Fields `course: Course`, `slot: TimeSlot` (`0..1`), `room: Room` (`0..1`), `type: SessionType`, `problemType: ProblemType` | None |
| `TimeSlot` | `src/TimeSlot.java` | Yes | Yes | No references (value class); used by `Session`, `Instructor`, `Room`, `SpecialRequest` | Overrides `equals()` and `hashCode()` for value equality. |
| `SpecialRequest` | `src/SpecialRequest.java` | Yes | Yes | Field `slot: TimeSlot` (`0..1`); `roomId` as `String`; `type: RequestType` | None |
| `Timetable` | `src/Timetable.java` | Yes | Yes | `ArrayList<Session> sessions` (aggregation) | None |
| `Scheduler` | `src/Scheduler.java` | Yes | Yes | Fields `timetable`, `checker: ConstraintChecker`, `rooms: ArrayList<Room>`, `requests: ArrayList<SpecialRequest>` | None. Method bodies are stubs where the work is planned for a later version. |
| `ConstraintChecker` | `src/ConstraintChecker.java` | Yes | Yes | Fields `timetable: Timetable`, `requests: ArrayList<SpecialRequest>` | Parameters of the nine private check methods are shortened to `(..)` in the diagram to keep it on one A4 page. |
| `ReportGenerator` | `src/ReportGenerator.java` | Yes | Yes | Field `timetable: Timetable` | None. Method bodies are stubs where the work is planned for a later version. |
| `SessionType` «enum» | `src/SessionType.java` | Yes (`THEORY`, `LAB`) | — | Used as attribute types in `Session`, `Room`, `SpecialRequest` | None |
| `RoomType` «enum» | `src/RoomType.java` | Yes (`CLASSROOM`, `LABORATORY`) | — | Used as attribute types in `Session`, `Room`, `SpecialRequest` | None |
| `RequestType` «enum» | `src/RequestType.java` | Yes (4 values) | — | Used as attribute types in `Session`, `Room`, `SpecialRequest` | None |
| `ProblemType` «enum» | `src/ProblemType.java` | Yes (`NO_ROOM`, `CONSTRAINT`) | — | Used as attribute types in `Session`, `Room`, `SpecialRequest` | None |

---

## 3. Running the Application

Command used:
```powershell
javac -d out src/*.java
java -cp out Main
```

Console output of `V1` (input: `1`, then `abc`, then `0`):
```text
==================================================
  DEPARTMENT COURSE TIMETABLE MANAGEMENT SYSTEM
         Software Engineering Department
==================================================
  1. Load input data
  2. Generate timetable automatically
  3. View timetable by year group
  4. View all scheduled sessions
  5. View unscheduled sessions and warnings
  6. Modify / remove an assignment
  7. Generate classroom request email
  8. Save timetable to file
  0. Exit
--------------------------------------------------
Select an option: 1

=== LOAD INPUT DATA ===
Reading data/courses.csv .... 6 courses loaded
Reading data/instructors.txt .... 5 instructors loaded
Reading data/rooms.csv .... 4 rooms loaded
Reading data/constraints.txt .... 5 requests loaded (4 mandatory, 1 preferred)

WARNINGS:
  courses.csv, line 8: invalid value "three" for theory hours -> record skipped
  constraints.txt, line 7: course "SENG499" not found -> request ignored

Data loaded with 2 warning(s). 10 session(s) created.
Press Enter to return to the main menu...

==================================================
  DEPARTMENT COURSE TIMETABLE MANAGEMENT SYSTEM
         Software Engineering Department
==================================================
  1. Load input data
  2. Generate timetable automatically
  3. View timetable by year group
  4. View all scheduled sessions
  5. View unscheduled sessions and warnings
  6. Modify / remove an assignment
  7. Generate classroom request email
  8. Save timetable to file
  0. Exit
--------------------------------------------------
Select an option: abc
Invalid choice "abc". Please enter a number from 0 to 8.
...
Select an option: 0
Goodbye.
```

---

## 4. Key Requirements

| ID | Requirement in one line |
| :--- | :--- |
| **FR-04** | Automatically assign every session to a day, start time and room so that all hard constraints `HC-01`–`HC-09` hold. |
| **FR-05** | Before every assignment, check `HC-01`–`HC-09` and reject any slot/room combination that violates one. |
| **FR-14** | Process mandatory fixed-time requests first and place them only at the requested day and time. |
| **FR-15** | On a conflict, try the next suitable room for the same slot, then the next slot. |
| **FR-16** | If nothing is valid, leave the session unscheduled with problem type `NO ROOM` or `CONSTRAINT` and the reason. |
| **FR-21** | A manual change goes through the same hard checks and is rejected with the HC code if it violates one. |
| **FR-07** | For a `NO ROOM` session, generate a classroom request email with course, students, room type and a suggested time. |
| **HC-05** | A room's capacity must be >= the course's number of students (e.g. `SENG101-L`, 70 students, has no laboratory). |

---

## 5. Algorithm and Conflict Handling

| Question | One-line answer | Details in SDD v2 |
| :--- | :--- | :--- |
| **In what order are sessions processed?** | Fixed-time sessions first; then by number of students (largest first) and length, theory before its lab. | §6.1 |
| **How are time slots selected?** | Mon→Fri, `09:00` → `17:00 − length`; preferred-time slots and (for a lab) slots after its theory are tried first. | §6.1 |
| **How are rooms selected?** | Rooms of the required type with `capacity >= students`, smallest first; a requested room only. | §6.1 |
| **How are the hard constraints checked?** | `ConstraintChecker.checkHard()` runs one method per `HC-01`–`HC-09` and returns the list of violations. | §6.4 |
| **What happens when a conflict is found?** | The next room for the same slot is tried, then the next slot; placed sessions are never moved. | §7 |
| **When is a session left unassigned?** | At once if no room of the right type/capacity exists (`NO ROOM`), else after all slots and rooms fail. | §6.1 |

---

## 6. GitHub Evidence

| Item | Value |
| :--- | :--- |
| **Repository** | https://github.com/keskinelifeylul/Department-Course-Timetable-Management-System- |
| **Total commits** | 11 (`git rev-list --count HEAD`) |
| **Tags / Versions** | `V1` (`code-v1` + `Code v2`), `V2` (`Checkpoint 2`), `V3` (`Final`) |
| **Date of first commit** | `2026-11-07` (`097c804`) |

### Key Commits

| Date | Author | Commit | Summary |
| :--- | :--- | :--- | :--- |
| `2026-11-07` | `keskinelifeylul` | `097c804` | Initial project upload (initial skeleton, data files, and design drafts) |
| `2026-11-09` | `Elif Eylül Keskin` | `4b5ac45` | Clean up legacy v1 design draft directory for SDD v2 alignment |
| `2026-11-11` | `Elif Eylül Keskin` | `0572c4e` | Reorganize milestone directory structure into `V1`, `V2`, `V3` |
| `2026-11-11` | `keskinelifeylul` | `ffd6671` | Complete 17-type SDD v2 Java architecture, `DataLoader`, and `ConsoleMenu` |
| `2026-11-11` | `keskinelifeylul` | `1fa4da2` | Merge main branch updates |

---

## 7. Team Contributions

| Member | Main contributions | Parts they can explain best |
| :--- | :--- | :--- |
| **Arda Güneş** | SRS v2 functional/constraint tables (`FR-01`–`FR-26`, `HC-01`–`HC-09`), domain classes (`Course`, `Instructor`, `Session`, `TimeSlot`), courses/instructors loading in `DataLoader`, and constraint checking logic in `ConstraintChecker`. | Domain model relationships, `Course.createSessions()`, `TimeSlot` overlap/working-hour checks, and `ConstraintChecker` rules (`HC-01`–`HC-09`, `SC-01`–`SC-03`). |
| **Elif Eylül Keskin** | SDD v2 class diagram & scheduling flowchart, `ConsoleMenu`, rooms/requests loading in `DataLoader`, greedy scheduling algorithm in `Scheduler`, and views/reports in `ReportGenerator`. | `DataLoader` validation & warning collection, `Scheduler` session ordering & room/slot selection, `ReportGenerator` 5×8 grids, and classroom request email generation. |

---

## 8. Plan to Checkpoint 2

| Version | Planned work | Requirements / constraints covered | Owner |
| :--- | :--- | :--- | :--- |
| **Code v2 (in V1)** | Input file processing and object management: `DataLoader` reads and validates the four files with warnings; `Course.createSessions()`; menu option 1 (Screen 2) | `FR-01`, `FR-02`, `FR-03`, `FR-09`–`FR-13`, `NFR-01`, `NFR-05` | Courses + instructors reader, `createSessions`: Arda Güneş · Rooms + requests reader, option 1 screen: Elif Eylül Keskin |
| **Code v3 (in V2)** | Initial scheduling algorithm and conflict detection: `Scheduler.sortSessions()`, `scheduleSession()`, `findRoom()`, `hasSuitableRoom()`; `HC-01`–`HC-06` in `ConstraintChecker`; `Room.isSuitableFor()` | `FR-04`, `FR-05`, `FR-15`, `FR-16`, `HC-01`–`HC-06` | `Scheduler`: Elif Eylül Keskin · `ConstraintChecker` `HC-01`–`HC-06`: Arda Güneş |
| **Code v4 (in V2)** | Working core scheduling: menu options 2–5 with log, summary, year grid, scheduled and unscheduled lists; regeneration | `FR-06`, `FR-18`, `FR-19`, `FR-20`, `FR-25`, `NFR-03` | Options 2 and 5: Arda Güneş · Options 3 and 4 (`ReportGenerator`): Elif Eylül Keskin |

---

## 9. Risks

- Fixed-time and specific-room requests (`HC-08`) are only planned for Code v5 (`V3`), but the sample data contains them. We make `Scheduler` skip mandatory `FIXED_TIME` and `SPECIFIC_ROOM` sessions in `V2` and list them as planned for `V3`, so the Checkpoint 2 demo does not show wrong placements.
- The greedy algorithm may leave a session unscheduled although a valid timetable exists (SDD §6.5). We test with the sample data by hand first (SDD §5.3 run) and explain the limitation instead of adding backtracking.
- Parsing the `|` separated TXT files and the `"Day HH-HH"` periods can fail on extra spaces or empty fields. Every field is trimmed, and each faulty line produces a warning with line number (`FR-02`); `TC-12` and `TC-16` are run with every change to `DataLoader`.
- Diagram and code can drift apart when methods are added. After every version we compare the class list with the Java files and update the SDD before tagging.
- Both members must be able to explain all code. Each member reviews and runs the other's changes before a tag, and we rotate the classes we write between versions.

---

## AI-Use Declaration

| Tool | Used for | How we checked the result |
| :--- | :--- | :--- |
| **Claude (Anthropic AI assistant)** | Help with revising the v1 text into v2, wording requirements as testable sentences, drafting tables and generating the diagrams from our design | Both members read every row, compared it with the project description and with the Java code of `code-v1`, and corrected what we did not agree with. We can explain every requirement, class and diagram element ourselves. |
