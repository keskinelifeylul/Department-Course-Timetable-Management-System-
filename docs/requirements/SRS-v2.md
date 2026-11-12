# Software Requirements Specification (SRS)

## Cover

| Field | Value |
| :--- | :--- |
| **Project** | Department Course Timetable Management System |
| **Document** | Software Requirements Specification |
| **Version** | v2 |
| **Date** | 10.11.2026 |
| **Team** | Arda Güneş (`ardagunes1907`) · Elif Eylül Keskin (`keskinelifeylul`) |
| **Member 1** | Arda Güneş · `ardagunes1907` |
| **Member 2** | Elif Eylül Keskin · `keskinelifeylul` |
| **Repository** | https://github.com/keskinelifeylul/Department-Course-Timetable-Management-System- |

## Revision History

| Version | Date | Author(s) | Summary of changes |
| :--- | :--- | :--- | :--- |
| **v1** | 30.10.2026 | Arda Güneş, Elif Eylül Keskin | Initial requirements from the project description |
| **v2** | 10.11.2026 | Arda Güneş, Elif Eylül Keskin | Requirements split into single, testable sentences (`FR-01`–`FR-08` reworded, `FR-09`–`FR-26` added); input file formats aligned with SDD v2 and the sample data; HC table corrected and violation messages added; assumptions with team decisions added; `NFR-03` made measurable, `NFR-05` added (see Section 8) |
| **Final** | | | |

---

## 1. Introduction

### 1.1 Purpose
This document defines what the Department Course Timetable Management System must do: load course, instructor, room and request data, automatically generate a weekly timetable, detect conflicts, and support the coordinator in resolving scheduling problems. It is written for the course instructor and the two-person development team, who will use it as the reference for design, implementation and testing.

### 1.2 Scope

**In Scope**
- Loading course, instructor, room and special-request data from external CSV and TXT files, with validation and warnings for faulty records.
- Splitting every course into a theory block and a laboratory block (sessions).
- Automatically generating a weekly timetable (Monday–Friday, `09:00–17:00`) for each of the four year groups.
- Assigning each session to a time slot and a suitable classroom or laboratory while respecting the hard constraints `HC-01`–`HC-09`.
- Considering the soft constraints `SC-01`–`SC-04` and showing a warning when one cannot be satisfied.
- Handling special scheduling requests (fixed time, specific room, unavailable day, laboratory before theory) as mandatory or preferred.
- Reporting sessions that cannot be assigned, separating room shortages (`NO ROOM`) from other problems (`CONSTRAINT`).
- Viewing, modifying and removing assignments with the same hard-constraint checks as automatic scheduling.
- Generating classroom request email drafts and saving timetables and drafts to text files.

**Out of Scope**
- Database integration and database management.
- Web-based, mobile or graphical (JavaFX/Swing) user interfaces; the system uses a console menu.
- Sending emails automatically; only a text draft is generated and saved.
- Student-level course registration, enrollment and elective-course conflict checking.
- Optimal timetables, numerical scoring of preferences and advanced optimization algorithms.
- Network communication and synchronization with external university systems.
- More than one user role, user accounts and passwords.

### 1.3 Definitions

| Term | Definition |
| :--- | :--- |
| **Session (block)** | One theory or laboratory block of a course, placed whole in consecutive one-hour slots on a single day and in one room, e.g. `SENG201-T` (theory) or `SENG201-L` (laboratory). |
| **Time slot** | A one-hour period between `09:00` and `17:00`, Monday to Friday (8 slots per day, 40 per week). A session occupies as many consecutive slots as its hours. |
| **Year group** | The students of one year of the program (Year 1, 2, 3 or 4). Courses of the same year group are assumed to share students and must not overlap. |
| **Coordinator** | The Department Course Timetable Coordinator, the only user of the system. |
| **Hard constraint** | A rule (`HC-01`–`HC-09`) that every assignment must satisfy. An assignment that violates a hard constraint is never made. |
| **Soft constraint** | A preference (`SC-01`–`SC-04`) that the system tries to satisfy. A violation is accepted but produces a warning. |
| **Special request** | An extra requirement for a course from `constraints.txt`: fixed time, specific room, unavailable day of the instructor or laboratory before theory. It is either `MANDATORY` (treated as a hard constraint) or `PREFERRED` (treated as a soft constraint). |
| **Unscheduled session** | A session that could not be assigned. Its problem type is `NO ROOM` (no room of the required type and capacity can be used) or `CONSTRAINT` (any other hard constraint or a mandatory request prevents it). |
| **Classroom request email** | A formal text draft to the Student Affairs Office asking for a room for a `NO ROOM` session. It is displayed and saved to a file, not sent. |

### 1.4 References
- SENG 271 Term Project: Department Course Timetable Management System (project description)
- SENG 271 Term Project Guide: Requirements, Design and Teamwork
- SENG 271 Tutorial: Java Project and GitHub Teamwork
- Software Design Document (SDD) v2 of our team, `docs/design/SDD-v2.md`

---

## 2. Overall Description

### 2.1 Product Perspective
The Department Course Timetable Management System is a stand-alone Java console application that helps the coordinator prepare the weekly timetable of a four-year Software Engineering program. It reads course, instructor, room and special-request data from external CSV and TXT files, automatically places every session in a time slot and a room while checking the scheduling constraints, and reports the sessions that cannot be placed. It does not connect to any other system; all input comes from files in the `data/` folder and all saved output goes to the `output/` folder.

### 2.2 User

| User | Main goals | Characteristics |
| :--- | :--- | :--- |
| **Department Course Timetable Coordinator** | To manage course, instructor, and classroom information; generate weekly timetables; check scheduling conflicts; review warnings; and obtain timetable reports. | Has basic computer skills and knowledge of the department's courses, instructors, classrooms, and scheduling requirements. Does not need programming knowledge to use the system. |

### 2.3 Operating Environment
The system is written in Java (JDK 21 or later) and uses only the standard Java library. It runs on any operating system with a Java runtime (Windows, Linux, macOS), from a terminal (`javac -d out src/*.java`, then `java -cp out Main`) or from IntelliJ IDEA. It is started from the repository root, reads its input files from `data/` and writes its output files to `output/`.

### 2.4 Assumptions and Constraints
- The working days are Monday to Friday; working hours are `09:00–17:00` in one-hour slots, and every session must end by `17:00`.
- `12:00–13:00` is a normal slot, not a lunch break, because the project description lists it as a slot. *(Question: is there a lunch break? Decision: no.)*
- Each course belongs to exactly one year group and has exactly one instructor; an instructor's courses are the courses that name the instructor in `courses.csv`.
- A course produces at most two sessions: one theory block with all theory hours and one laboratory block with all laboratory hours. A block with 0 hours is not created.
- The required room type is derived from the block: a theory block needs a `Classroom` and a laboratory block needs a `Laboratory`. *(Question: how is the room type given for a course with both kinds of hours? Decision: by block type, so `courses.csv` has no room-type field.)*
- A laboratory block may be on the same day as its theory block if it starts after the theory block ends. *(Question: same day or only a later day? Decision: same day allowed.)*
- `"Morning"` means a session that ends by `13:00`; `"afternoon"` means a session that starts at `13:00` or later.
- A gap (`SC-03`) is an empty slot between two sessions of the same year group on the same day.
- A mandatory request that conflicts with a hard constraint is never moved by the system; it is reported to the coordinator, who resolves it.
- Input files are plain text in UTF-8, stored in `data/`; the first line of a CSV file is a header, and lines starting with `#` in TXT files are comments.

---

## 3. Functional Requirements

*Priority: Must = required by the project description · Should = preference or optional feature.*

| ID | Group | Requirement | Priority | Source | Screen |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **FR-01** | Data loading | The system shall read course data (code, title, year, instructor ID, theory hours, laboratory hours, number of students) from `data/courses.csv`. | Must | §3.1, §3.7 | Screen 2 |
| **FR-02** | Data loading | If a record contains an invalid value, the system shall skip the record and display a warning with the file name, line number and invalid value, without terminating. | Must | §3.7 | Screen 2 |
| **FR-03** | Data loading | The system shall read instructor data (ID, name, unavailable periods, preferred teaching time) from `data/instructors.txt`. | Must | §3.2, §3.7 | Screen 2 |
| **FR-04** | Automatic generation | The system shall automatically assign every session to a day, start time and room so that all hard constraints `HC-01`–`HC-09` are satisfied. | Must | §3.8 | Screen 3 |
| **FR-05** | Conflict handling | Before every assignment, the system shall check `HC-01`–`HC-09` and reject a time slot and room combination that violates any of them. | Must | §3.5, §3.8 | Screen 3 |
| **FR-06** | Viewing | The system shall display the weekly timetable of a selected year group as a 5-day × 8-slot grid, followed by that year group's unscheduled sessions. | Must | §3.9, §3.11 | Screen 4 |
| **FR-07** | Classroom request | For a selected `NO ROOM` session, the system shall generate a classroom request email draft stating the course code and title, number of students, required room type and a suggested time. | Must | §3.10 | Screen 7 |
| **FR-08** | Output | The system shall save the timetables of all year groups and the list of unscheduled sessions to a text file in `output/`. | Should | §3.11 | Option 8 |
| **FR-09** | Data loading | The system shall read room data (ID, type, capacity, unavailable periods) from `data/rooms.csv`. | Must | §3.4, §3.7 | Screen 2 |
| **FR-10** | Data loading | The system shall read special requests from `data/constraints.txt` and classify each one as `MANDATORY` or `PREFERRED`. | Must | §3.6, §3.7 | Screen 2 |
| **FR-11** | Data loading | If an input file is missing, the system shall display an error message naming the file and continue with the other files. | Must | §3.7 | Screen 2 |
| **FR-12** | Data loading | If a record refers to a course, instructor or room that does not exist, the system shall ignore the record and display a warning. | Must | §3.7 | Screen 2 |
| **FR-13** | Automatic generation | The system shall create from each course one theory session and one laboratory session (only for blocks with more than 0 hours), each placed whole in consecutive slots on one day. | Must | §3.3 | Screen 3 |
| **FR-14** | Automatic generation | The system shall process mandatory fixed-time requests before all other sessions and place such a session only at the requested day and time. | Must | §3.6, §3.8 | Screen 3 |
| **FR-15** | Conflict handling | When a time slot and room combination is rejected, the system shall try the next suitable room for the same slot and then the next time slot, until a valid combination is found or all combinations have been tried. | Must | §3.8 | Screen 3 |
| **FR-16** | Conflict handling | If no valid combination exists, the system shall leave the session unscheduled and record its problem type (`NO ROOM` or `CONSTRAINT`) and the reason. | Must | §3.8, §3.10 | Screens 3, 5 |
| **FR-17** | Conflict handling | When a soft constraint cannot be satisfied, the system shall keep the assignment and record a warning that names the SC code. | Should | §3.5.2 | Screens 3, 5 |
| **FR-18** | Automatic generation | After generation, the system shall display one result line per session (`OK`, `FAIL` or `WARN`) and a summary with the numbers of sessions, scheduled sessions, unscheduled sessions and warnings. | Must | §3.8 | Screen 3 |
| **FR-19** | Viewing | The system shall display all scheduled sessions with session name, course title, year group, instructor, day, start–end time and room. | Must | §3.9, §3.11 | Option 4 |
| **FR-20** | Viewing | The system shall display the unscheduled sessions with year, hours, students, problem type and reason, `NO ROOM` sessions separately marked, followed by the soft-constraint warnings. | Must | §3.9, §3.10 | Screen 5 |
| **FR-21** | Modification | The system shall allow the coordinator to move a session to a new day, start time and room, and shall reject the change with the violated HC code if it violates a hard constraint. | Must | §3.9 | Screen 6 |
| **FR-22** | Modification | If a manual change satisfies all hard constraints but violates a soft constraint, the system shall show the warning and apply the change only after the coordinator confirms. | Should | §3.9 | Screen 6 |
| **FR-23** | Modification | The system shall allow the coordinator to remove the assignment of a session, after which the session is listed as unscheduled. | Must | §3.9 | Screen 6 |
| **FR-24** | Classroom request | When the coordinator confirms, the system shall save the email draft to `output/classroom_request_<course code>.txt`. | Must | §3.10 | Screen 7 |
| **FR-25** | Automatic generation | The system shall allow the coordinator to regenerate the timetable; regeneration removes all previous assignments and warnings first. | Should | §3.9 | Option 2 |
| **FR-26** | Classroom request | For a `NO ROOM` session, the system shall suggest the first time at which both its year group and its instructor are free. | Should | §3.10 | Screen 7 |

---

## 4. Scheduling Constraints

### 4.1 Hard Constraints (must never be violated)

| ID | Constraint | Rule in our own words | Data needed to check it | Message on violation (v2) |
| :--- | :--- | :--- | :--- | :--- |
| **HC-01** | Instructor conflict | A session cannot be placed at a time when its instructor already teaches another session that overlaps it on the same day. | Instructor of the course; day, start time and length of all sessions already assigned to that instructor | `HC-01 Instructor conflict: Dr. Kaya already teaches SENG101-T on Mon 09:00-12:00` |
| **HC-02** | Room conflict | A room cannot be given to a session if any hour overlaps another session already placed in the same room on the same day. | Room; day, start time and length of all sessions already assigned to that room | `HC-02 Room conflict: B202 is used by SENG401-L on Tue 13:00-15:00` |
| **HC-03** | Year-group conflict | Two sessions of courses of the same year group cannot overlap, because the same students attend them. | Year group of the course (courses file); day, start time and length of all assigned sessions of that year group | `HC-03 Year-group conflict: Year 2 already has SENG203-T on Mon 09:00-12:00` |
| **HC-04** | Working hours | A session must be on Monday–Friday, start at `09:00` or later and end by `17:00`. A block that would end after `17:00` cannot start at that time. | Day, start time and length (hours) of the session | `HC-04 Working hours: SENG101-T (3 h) cannot start at 15:00; sessions must be Mon-Fri 09:00-17:00` |
| **HC-05** | Room capacity | A session may be placed only in a room whose capacity is equal to or greater than the estimated number of students of the course. | Estimated number of students (courses file); room capacity (rooms file) | `HC-05 Room capacity: B201 (capacity 30) is too small for SENG201-L (55 students)` |
| **HC-06** | Room type | A theory session must be placed in a `Classroom` and a laboratory session in a `Laboratory`. | Session type (theory or laboratory); room type (rooms file) | `HC-06 Room type: SENG201-L requires a Laboratory, but A101 is a Classroom` |
| **HC-07** | Instructor availability | A session cannot cover any hour in which its instructor is unavailable, including days given by an `UNAVAILABLE_DAY` request. | Instructor's unavailable periods (instructors file and constraints file); day, start time and length of the session | `HC-07 Instructor availability: Dr. Sahin is unavailable on Friday` |
| **HC-08** | Fixed-time requirements | A session with a mandatory fixed-time request can only be placed at exactly the requested day and time. If that time violates another hard constraint, the session stays unscheduled and the problem is reported. | Mandatory `FIXED_TIME` requests (course, session type, day, start and end time) | `HC-08 Fixed time: SENG305-T must be on Fri 09:00-12:00` |
| **HC-09** | Room availability | A room cannot be used during any hour in which it is marked as unavailable. | Room's unavailable periods (rooms file); day, start time and length of the session | `HC-09 Room availability: A102 is unavailable on Thu 09:00-11:00` |

### 4.2 Soft Constraints (preferences; a violation produces a warning)

| ID | Constraint | How our system considers it | Warning shown |
| :--- | :--- | :--- | :--- |
| **SC-01** | Theory before laboratory | For a laboratory session, slots that start after the end of the course's theory session are tried first. If only an earlier slot is valid, it is used with a warning. Not applied to a course with a `LAB_BEFORE_THEORY` request. | `SC-01 SENG201-L is placed before SENG201-T (Mon 09:00-11:00 < Tue 09:00-12:00)` |
| **SC-02** | Instructor preferences | For an instructor with a `MORNING` or `AFTERNOON` preference, slots in the preferred part of the day are tried first; otherwise the first valid slot is used with a warning. Preferred requests (e.g. a preferred room) are handled the same way. | `SC-02 SENG401-T: Dr. Aksoy prefers morning hours; placed at Mon 13:00-15:00` |
| **SC-03** | Avoiding unnecessary gaps | Slots are tried from `09:00` onwards, so sessions of a year group are packed in the morning. After generation and after every manual change, the system reports each gap in a year group's day. | `SC-03 Year 2 will have a 3-hour gap on Monday (12:00-15:00)` |
| **SC-04** | Consecutive teaching hours | Every session is placed whole in consecutive slots (`FR-13`), so this preference is always satisfied for a block; no other grouping is attempted. | None (always satisfied) |

### 4.3 Special Scheduling Requests

| Request type | Example | Mandatory or preferred | How the system handles it |
| :--- | :--- | :--- | :--- |
| **Fixed time (`FIXED_TIME`)** | `SENG301-T` must be on Wednesday, `13:00–16:00` | Mandatory | Processed first and placed only at the requested time, in the first suitable room (`HC-08`). If any hard constraint fails there, the session stays unscheduled and is reported; it is never moved. |
| **Specific room (`SPECIFIC_ROOM`)** | `SENG401-L` must use Laboratory `B202` | Mandatory | Only the requested room is tried, at every candidate slot. If the room is never valid, the session stays unscheduled as `CONSTRAINT` with the reason. |
| **Unavailable day (`UNAVAILABLE_DAY`)** | `SENG201`: instructor unavailable on Friday | Mandatory | Added to the instructor's unavailable periods (`09:00–17:00` of that day) and checked as `HC-07`. |
| **Laboratory before theory (`LAB_BEFORE_THEORY`)** | `SENG203`: laboratory may precede theory | Preferred | `SC-01` is not applied to this course, so no `SC-01` warning is produced. |
| **Preferred version of the types above** | `FIXED_TIME … \| PREFERRED` | Preferred | The requested time or room is tried first; if it is not valid, other slots or rooms are tried and an `SC-02` warning is shown. |

---

## 5. Input Data Requirements

| File | Format | Fields (in order) | Example line | Validation rules |
| :--- | :--- | :--- | :--- | :--- |
| `data/courses.csv` | CSV, comma-separated, first line is a header | `code, title, year, instructorId, theoryHours, labHours, students` | `SENG201,Object-Oriented Programming,2,I02,3,2,55` | 7 fields; code is unique and not empty; year is 1–4; `theoryHours` and `labHours` are whole numbers >= 0 and not both 0; `students > 0`; `instructorId` exists in `instructors.txt`. |
| `data/instructors.txt` | TXT, fields separated by `\|`, `#` starts a comment | `instructorId \| name \| unavailable periods \| preferred time` | `I04 \| Dr. Sahin \| Fri \| NONE` | 4 fields; ID unique and not empty; name not empty; unavailable periods are empty or a `;`-separated list of `Day` (`Mon`–`Fri`) or `Day HH-HH` with `09 <= start < end <= 17`; preferred time is `MORNING`, `AFTERNOON` or `NONE`. |
| `data/rooms.csv` | CSV, comma-separated, first line is a header | `roomId, type, capacity, unavailable periods` | `A102,CLASSROOM,40,Thu 09-11` | 4 fields (last may be empty); ID unique; type is `CLASSROOM` or `LABORATORY`; capacity is a whole number > 0; unavailable periods as in `instructors.txt`. |
| `data/constraints.txt` | TXT, fields separated by `\|`, `#` starts a comment | `requestType \| courseCode \| session (T, L or -) \| value \| MANDATORY or PREFERRED` | `FIXED_TIME \| SENG301 \| T \| Wed 13-16 \| MANDATORY` | 5 fields; type is `FIXED_TIME`, `SPECIFIC_ROOM`, `UNAVAILABLE_DAY` or `LAB_BEFORE_THEORY`; course exists; value is `Day HH-HH` (`FIXED_TIME`, length = session hours), an existing room ID (`SPECIFIC_ROOM`), a day (`UNAVAILABLE_DAY`) or `-`; last field is `MANDATORY` or `PREFERRED`. |

### Input error handling

| Error | Example | System reaction |
| :--- | :--- | :--- |
| **Missing file** | `rooms.csv` not found | Error message `"data/rooms.csv not found"`; the other files are still read. Generation (option 2) is refused with a message while courses or rooms are missing (`FR-11`). |
| **Invalid numeric value** | `"three"` for theory hours | The record is skipped; warning with file name, line number and value: `courses.csv, line 8: invalid value "three" for theory hours -> record skipped` (`FR-02`). |
| **Incomplete record** | A course line with 6 instead of 7 fields | The record is skipped with a warning naming the file, line and expected number of fields (`FR-02`). |
| **Reference to a nonexistent item** | A request for course `SENG499`, which is not in `courses.csv` | The request is ignored with a warning: `constraints.txt, line 7: course "SENG499" not found -> request ignored` (`FR-12`). |
| **Duplicate ID or invalid code** | Two rooms with ID `A101`; room type `HALL` | The second record is skipped with a warning (`FR-02`). |

---

## 6. Output Requirements

| Output | Content | Shown on screen | Saved to file |
| :--- | :--- | :--- | :--- |
| **Generation log** | One line per session: `[OK]`, `[FAIL]` with reason, `[WARN]` with SC code; summary counts | Yes | No |
| **Weekly timetable per year group** | 5 × 8 grid with session name and room; session details (title, instructor, day, time, room); unscheduled sessions of the year | Yes | Yes (`output/timetable.txt`) |
| **All scheduled sessions** | Session name (e.g., `SENG201-T`), course title, year group, instructor, day, start–end time, room | Yes | Yes (`output/timetable.txt`) |
| **Unscheduled sessions** | Session, year, hours, students, problem type (`NO ROOM` / `CONSTRAINT`), reason | Yes | Yes (`output/timetable.txt`) |
| **Warnings** | Soft-constraint warnings and input-file warnings | Yes | Yes (`output/timetable.txt`) |
| **Classroom request email draft** | Subject, course code and title, number of students, required room type, suggested time | Yes | Yes (`output/classroom_request_<code>.txt`) |

---

## 7. Non-Functional Requirements

| ID | Category | Requirement |
| :--- | :--- | :--- |
| **NFR-01** | Reliability | The system shall not terminate unexpectedly on invalid input data or invalid user input; it shall show an understandable message instead. |
| **NFR-02** | Usability | The system shall reject an invalid menu choice (a non-number or a number outside `0–8`) with a message and show the menu again. |
| **NFR-03** | Performance | The system shall generate the timetable for the sample data (6 courses, 4 rooms) within 5 seconds on a standard laptop. |
| **NFR-04** | Portability | The system shall compile and run with `javac` and `java` (JDK 21 or later) from the repository root, using only the standard Java library. |
| **NFR-05** | Usability | Every violation, warning and input error message shall name the constraint code (`HC-xx` or `SC-xx`) or the file and line number it refers to. |

---

## 8. Changes from the Previous Version

| ID or section | Change (added / changed / removed) | Reason for the change |
| :--- | :--- | :--- |
| **Cover, Revision History** | Changed: version v2, members and v2 row | New version |
| **FR-01, FR-02** | Changed: `FR-01` lists the fields; `FR-02` now says which details the warning shows | We could not write an exact expected result for the v1 sentences |
| **FR-03** | Changed: from `"load and validate course, instructor and classroom information"` to reading `instructors.txt` | v1 contained three requirements in one sentence; rooms moved to `FR-09` |
| **FR-04, FR-05** | Changed: made measurable (all of `HC-01`–`HC-09`, checked before every assignment) | `"Based on the provided data"` and `"detect conflicts"` could not be tested |
| **FR-06** | Changed: only viewing a year group; modification moved to `FR-21`–`FR-23` | v1 had two behaviours in one sentence |
| **FR-07** | Changed: from `"process classroom requests"` to generating the email draft | Matches §3.10 and Screen 7 |
| **FR-08** | Changed: only saving the timetable; displaying moved to `FR-18`–`FR-20`, saving the email to `FR-24` | v1 had five outputs in one sentence |
| **FR-09–FR-26** | Added | Rooms and requests files, missing file, unknown references, session creation, fixed-time processing, alternatives, unscheduled sessions, warnings, views, modification, saving the email, regeneration and suggested time were not covered in v1 |
| **1.2, 1.3, 2.4** | Changed/added: scope lists, five new terms, ten assumptions with team decisions | Questions found while designing SDD v2 (lunch break, room type, lab on the same day, morning) |
| **4.1 HC-03, HC-05** | Corrected: duplicated data text (`HC-03`) and cut-off rule (`HC-05`); messages added for all HCs | Errors in v1; message column is required from v2 |
| **4.2, 4.3** | Changed: describes how the system considers each SC and each request type, including preferred requests | v1 only repeated the names |
| **Section 5** | Changed: `instructors.txt` now stores unavailable periods (not available days); `rooms.csv` has no name field but unavailable periods; `constraints.txt` has a session field; `courses.csv` has no `roomType` field | Needed for `HC-07`, `HC-09` and requests per session; room type is derived from the block (2.4) |
| **NFR-03** | Changed: `"reasonable time"` → within 5 seconds for the sample data | Not testable in v1 |
| **NFR-04, NFR-05** | `NFR-04` replaced (code organisation belongs in the SDD) by portability; `NFR-05` added | v1 `NFR-04` was a design statement |

---

## AI-Use Declaration

| Tool | Used for | How we checked the result |
| :--- | :--- | :--- |
| **Claude (Anthropic AI assistant)** | Help with revising the v1 text into v2, wording requirements as testable sentences, drafting tables and generating the diagrams from our design | Both members read every row, compared it with the project description and with the Java code of `code-v1`, and corrected what we did not agree with. We can explain every requirement, class and diagram element ourselves. |
