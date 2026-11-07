package v1;

import java.util.ArrayList;
import java.util.List;

/**
 * Scheduler.java (Code v1 - Project Skeleton)
 * Coordinates data loading, automatic timetable generation, conflict detection,
 * manual assignment modification, and classroom request email generation.
 */
public class Scheduler {
    private List<Course> courses;
    private List<Instructor> instructors;
    private List<Room> rooms;
    private Timetable timetable;

    public Scheduler(Timetable timetable) {
        this.courses = new ArrayList<>();
        this.instructors = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.timetable = timetable;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<Instructor> getInstructors() {
        return instructors;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    public void loadInputData() {
        System.out.println("\n=== LOAD INPUT DATA ===");
        System.out.println("[Code v1 Skeleton] Initializing sample objects (Course, Instructor, Room)...");
        courses.clear();
        instructors.clear();
        rooms.clear();

        courses.add(new Course("SENG101", "Introduction to Software Engineering", 1, 3, 2, 70, "INST01", "Classroom"));
        instructors.add(new Instructor("INST01", "Dr. Kaya", "NONE"));
        rooms.add(new Room("A101", "Classroom", 80));

        System.out.println("Sample entities created: " + courses.size() + " course(s), "
                + instructors.size() + " instructor(s), " + rooms.size() + " room(s).");
    }

    public void generateTimetableAutomatically() {
        System.out.println("\n=== AUTOMATIC TIMETABLE GENERATION ===");
        System.out.println("[Code v1 Skeleton] Automatic scheduling algorithm structure initialized.");
    }

    public boolean checkHardConstraints(Course course, TimeSlot slot, Room room) {
        if (slot == null || !slot.isWithinWorkingHours()) {
            return false;
        }
        if (room == null || !room.hasSufficientCapacity(course.getStudentCount())) {
            return false;
        }
        return true;
    }

    public boolean checkSoftConstraints(Course course, TimeSlot slot) {
        return true;
    }

    public void modifyOrRemoveAssignment(String sessionCode, TimeSlot newSlot, Room newRoom) {
        System.out.println("\n=== MODIFY / REMOVE AN ASSIGNMENT ===");
        System.out.println("[Code v1 Skeleton] Manual modification method called for " + sessionCode);
    }

    public void generateClassroomRequestEmail() {
        System.out.println("\n=== CLASSROOM REQUEST ===");
        System.out.println("[Code v1 Skeleton] Classroom request email generator initialized.");
    }
}


