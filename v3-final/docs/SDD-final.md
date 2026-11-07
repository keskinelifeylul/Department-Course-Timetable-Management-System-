# Software Design Document (SDD — Final)
**Project:** SENG 271 — Department Course Timetable Management System

---

## 1. Architectural Overview & Revision History

| Version | Week | Summary of Design Updates |
| :--- | :---: | :--- |
| **SDD v1** | Week 4 | Initial UML Class Diagram and Scheduling Algorithm Flowchart. |
| **SDD v2** | Week 6 | Added `Session`, `SpecialRequest`, and `DataLoader` classes to separate file I/O from domain entities and support separate `-T` and `-L` blocks. |
| **SDD Final** | Week 13 | Updated `Scheduler` and `Timetable` methods to match final implementation including best-fit room selection, two-pass soft-constraint handling, manual modification verification, and classroom request email generation. |

---

## 2. A. UML Class Diagram

```mermaid
classDiagram
    class Main {
        -DataLoader dataLoader
        -Timetable timetable
        -Scheduler scheduler
        -Scanner scanner
        +main(String[] args) void
        +runInteractiveMenu() void
        +runAutomatedDemo() void
    }

    class DataLoader {
        -Map~String, Course~ courses
        -Map~String, Instructor~ instructors
        -Map~String, Room~ rooms
        -List~SpecialRequest~ specialRequests
        -List~String~ loadWarnings
        +loadAllData(String dataDir) void
        +getCourses() Map~String, Course~
        +getInstructors() Map~String, Instructor~
        +getRooms() Map~String, Room~
        +getSpecialRequests() List~SpecialRequest~
    }

    class Course {
        -String courseCode
        -String title
        -int yearGroup
        -int theoryHours
        -int labHours
        -int studentCount
        -String instructorId
        -String requiredRoomType
        +createSessions() List~Session~
    }

    class Session {
        -String sessionId
        -Course course
        -boolean isLab
        -int durationHours
        -String requiredRoomType
        -TimeSlot assignedTimeSlot
        -Room assignedRoom
        -boolean scheduled
        -String problemType
        -String unscheduledReason
        +assign(TimeSlot slot, Room room) void
        +unassign(String problemType, String reason) void
    }

    class Instructor {
        -String instructorId
        -String name
        -List~String~ assignedCourseCodes
        -List~TimeSlot~ unavailableSlots
        -String preferredTime
        +isAvailable(TimeSlot candidate) boolean
        +satisfiesPreference(TimeSlot candidate) boolean
    }

    class Room {
        -String roomId
        -String roomType
        -int capacity
        -List~TimeSlot~ unavailableSlots
        +hasSufficientCapacity(int studentCount) boolean
        +matchesType(String requiredType) boolean
        +isAvailable(TimeSlot candidate) boolean
    }

    class TimeSlot {
        -String day
        -int startHour
        -int endHour
        +isWithinWorkingHours() boolean
        +overlapsWith(TimeSlot other) boolean
        +isAfter(TimeSlot other) boolean
        +parse(String spec)$ TimeSlot
    }

    class SpecialRequest {
        -String courseCode
        -boolean mandatory
        -String constraintType
        -String value
    }

    class Timetable {
        -List~Session~ allSessions
        -List~String~ softWarnings
        +getScheduledSessions() List~Session~
        +getUnscheduledSessions() List~Session~
        +getRoomShortageSessions() List~Session~
        +printYearGroupTimetable(int yearGroup, Map instructors) void
        +saveToFile(String outputDir, Map instructors) void
    }

    class Scheduler {
        -DataLoader dataLoader
        -Timetable timetable
        +generateTimetable() void
        +checkAllHardConstraints(Session s, TimeSlot ts, Room r, Map inst) String
        +evaluateSoftConstraints(Session s, TimeSlot ts, Map inst) List~String~
        +findSuggestedTimeForRoomRequest(Session s) TimeSlot
        +generateClassroomRequestEmail(Session s, TimeSlot ts) String
    }

    Main --> DataLoader : uses
    Main --> Timetable : uses
    Main --> Scheduler : uses
    Scheduler --> DataLoader : reads entities
    Scheduler --> Timetable : populates
    DataLoader "1" *-- "*" Course : loads
    DataLoader "1" *-- "*" Instructor : loads
    DataLoader "1" *-- "*" Room : loads
    DataLoader "1" *-- "*" SpecialRequest : loads
    Course "1" --> "*" Session : creates (-T, -L)
    Session --> TimeSlot : assignedTimeSlot
    Session --> Room : assignedRoom
    Instructor "1" *-- "*" TimeSlot : unavailableSlots
    Room "1" *-- "*" TimeSlot : unavailableSlots
    Timetable "1" *-- "*" Session : manages
```

---

## 3. B. Scheduling Algorithm Flowchart

```mermaid
flowchart TD
    Start(["Start Automatic Timetable Generation"]) --> LoadData["1. Load Course, Instructor, Room, and SpecialRequest Data"]
    LoadData --> CreateSessions["Create Consecutive Teaching Blocks (-T and -L Sessions)"]
    CreateSessions --> CheckFixed{"2. Any Unprocessed Fixed-Time Session?"}

    CheckFixed -- "Yes" --> ValidateFixed["Check HC-01, HC-03, HC-04, HC-07 and Capable Rooms (HC-05, HC-06, HC-09)"]
    ValidateFixed --> FixedValid{"All Hard Constraints Satisfied?"}
    FixedValid -- "Yes" --> AssignFixed["9. Record Fixed-Time Assignment & Check Soft Constraints"]
    FixedValid -- "No" --> FailFixed["10. Mark Unscheduled (CONSTRAINT / NO ROOM) & Record Reason"]
    AssignFixed --> CheckFixed
    FailFixed --> CheckFixed

    CheckFixed -- "No" --> NextRem{"3. Select Next Unscheduled Session"}
    NextRem -- "More Sessions" --> FilterRooms["5. Filter Capable Rooms (HC-05 Capacity & HC-06 Type, Best-Fit Order)"]
    FilterRooms --> HasCapableRoom{"Any Room with Sufficient Capacity?"}
    HasCapableRoom -- "No" --> RecordNoRoom["10. Mark Unscheduled: Problem Type = NO ROOM (HC-05)"]
    RecordNoRoom --> NextRem

    HasCapableRoom -- "Yes" --> SearchLoop["4. Search Candidate Days (Mon-Fri) & Time Slots (09:00-17:00) & Capable Rooms"]
    SearchLoop --> CheckHC["6-7. Check Hard Constraints (HC-01, HC-02, HC-03, HC-04, HC-07, HC-09)"]
    CheckHC --> HCPass{"Hard Constraints Satisfied?"}
    HCPass -- "No (Conflict)" --> TryAlt{"8. More Candidate Slots/Rooms?"}
    TryAlt -- "Yes" --> SearchLoop
    TryAlt -- "No" --> Pass2Check{"Was this Pass 1 (Strict Soft Constraints)?"}

    HCPass -- "Yes" --> SoftCheck{"Check Soft Constraints (SC-01, SC-02, SC-03)"}
    SoftCheck -- "Satisfied (or Pass 2)" --> RecordAssign["9. Record Assignment; Log Soft Warning if Violated"]
    SoftCheck -- "Violated in Pass 1" --> TryAlt

    Pass2Check -- "Yes" --> StartPass2["Retry Search in Pass 2 (Allow Soft-Constraint Warnings)"]
    StartPass2 --> SearchLoop
    Pass2Check -- "No" --> RecordFail["10. Mark Unscheduled: Problem Type = CONSTRAINT"]

    RecordAssign --> NextRem
    RecordFail --> NextRem
    NextRem -- "All Sessions Processed" --> Summary["11. Print Timetable Summary & Scheduling Report"]
    Summary --> EndNode(["End Automatic Timetable Generation"])
```

---

## 4. C. Class Descriptions

1. **`Main`**: Provides the console menu interface (Options 1–8 and 0) and coordinates user interactions and automated demonstration (`--demo`).
2. **`DataLoader`**: Parses `courses.csv`, `instructors.txt`, `rooms.csv`, and `constraints.txt`. Validates numeric fields and cross-references, logging clear warnings without terminating on faulty lines.
3. **`Course`**: Encapsulates curriculum metadata and generates `-T` (Theory) and `-L` (Laboratory) `Session` objects.
4. **`Session`**: Represents an atomic, consecutive teaching block to be scheduled into a `TimeSlot` and `Room`, storing assignment state or failure diagnostics (`NO ROOM` vs `CONSTRAINT`).
5. **`Instructor`**: Stores instructor identity, unavailable `TimeSlot` blocks, and morning/afternoon preferences.
6. **`Room`**: Represents a physical `Classroom` or `Laboratory` with student capacity and availability checks.
7. **`TimeSlot`**: Models a time interval on a working day (`Mon`–`Fri`, `09:00`–`17:00`) and provides overlap and ordering calculations.
8. **`SpecialRequest`**: Represents mandatory or preferred constraints loaded from `constraints.txt`.
9. **`Timetable`**: Maintains all scheduled and unscheduled sessions, formats year-group ASCII grids, and exports schedules to `.txt` and `.csv`.
10. **`Scheduler`**: Executes the 2-step automatic scheduling algorithm, checks hard (`HC-01`–`HC-09`) and soft (`SC-01`–`SC-04`) constraints, validates manual edits, and generates classroom request emails.

---

## 5. D. Key Design Decisions & Algorithm Limitations

1. **Best-Fit Room Selection**: Capable rooms are sorted in ascending order of capacity (`capacity >= students`). This prevents a 30-student class from occupying an 80-seat classroom (`A101`) that is needed by a 70-student class (`SENG101-T`).
2. **Two-Pass Soft-Constraint Resolution**: For each session, Pass 1 searches for a `(TimeSlot, Room)` pair that satisfies all hard constraints *and* all soft constraints (`SC-01`, `SC-02`, `SC-03`). If no ideal slot exists, Pass 2 places the session in the first hard-constraint-valid slot and records the corresponding soft-constraint warning.
3. **Greedy Rule-Based Limitation**: Because the algorithm assigns sessions sequentially without global backtracking, an early session assignment could occupy a slot needed by a later course. However, fixed-time requests are processed first (Step 1) and rooms use best-fit ordering to minimize greedy conflicts.

