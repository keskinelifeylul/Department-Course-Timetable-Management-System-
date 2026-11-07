# Software Design Document (SDD v2 — Checkpoint 1 / Week 6)
**Project:** SENG 271 — Department Course Timetable Management System

---

## A. UML Class Diagram (Consistent with Code v1 Skeleton)

```mermaid
classDiagram
    class Main {
        +main(String[] args) void
        -printMainMenu() void
    }

    class Course {
        -String courseCode
        -String title
        -int yearGroup
        -int theoryHours
        -int labHours
        -int studentCount
        -String assignedInstructorId
        -String requiredRoomType
        +getCourseCode() String
        +getYearGroup() int
        +getStudentCount() int
    }

    class Instructor {
        -String instructorId
        -String name
        -List~String~ assignedCourses
        -List~TimeSlot~ unavailableSlots
        -String preferredTimes
        +addAssignedCourse(String courseCode) void
        +addUnavailableSlot(TimeSlot slot) void
        +isAvailable(TimeSlot slot) boolean
    }

    class Room {
        -String roomId
        -String roomType
        -int capacity
        -List~TimeSlot~ unavailableSlots
        +hasSufficientCapacity(int studentCount) boolean
        +matchesType(String requiredType) boolean
        +isAvailable(TimeSlot slot) boolean
    }

    class TimeSlot {
        -String day
        -int startHour
        -int endHour
        +getDuration() int
        +isWithinWorkingHours() boolean
        +overlapsWith(TimeSlot other) boolean
    }

    class Timetable {
        -List~String~ scheduledSessions
        -List~String~ unscheduledSessions
        -List~String~ warnings
        +displayTimetableByYear(int yearGroup) void
        +displayAllScheduledSessions() void
        +displayUnscheduledAndWarnings() void
        +saveToFile(String filePath) void
    }

    class Scheduler {
        -List~Course~ courses
        -List~Instructor~ instructors
        -List~Room~ rooms
        -Timetable timetable
        +loadInputData() void
        +generateTimetableAutomatically() void
        +checkHardConstraints(Course c, TimeSlot ts, Room r) boolean
        +checkSoftConstraints(Course c, TimeSlot ts) boolean
        +modifyOrRemoveAssignment(String code, TimeSlot ts, Room r) void
        +generateClassroomRequestEmail() void
    }

    Main --> Scheduler : uses
    Main --> Timetable : uses
    Scheduler --> Timetable : updates
    Scheduler "1" *-- "*" Course : manages
    Scheduler "1" *-- "*" Instructor : manages
    Scheduler "1" *-- "*" Room : manages
    Instructor "1" *-- "*" TimeSlot : unavailableSlots
    Room "1" *-- "*" TimeSlot : unavailableSlots
```

---

## B. Scheduling Algorithm Flowchart

```mermaid
flowchart TD
    A(["Start"]) --> B["1. Load Course, Instructor, Room, and Constraint Data"]
    B --> C["2. Identify Courses with Fixed-Time Requirements"]
    C --> D["3. Process Fixed-Time Assignments & Check Validity"]
    D --> E{"4. Any Unscheduled Session Left?"}
    E -- "Yes" --> F["5. Search Available Time Slots (Mon-Fri, 09:00-17:00)"]
    F --> G["6. Search Suitable Classrooms / Laboratories"]
    G --> H{"7. All Hard Constraints (HC-01..HC-09) Satisfied?"}
    H -- "No (Conflict)" --> I{"8. Alternative Time Slot or Room Available?"}
    I -- "Yes" --> F
    I -- "No" --> J["10. Record Session as Unscheduled with Reason"]
    H -- "Yes" --> K["9. Consider Soft Constraints & Record Scheduled Session"]
    J --> E
    K --> E
    E -- "No" --> L["11. Generate Weekly Timetables & Scheduling Report"]
    L --> M(["End"])
```

