package v1;

import java.util.ArrayList;
import java.util.List;

/**
 * Instructor.java (Code v1 - Project Skeleton)
 * Represents an instructor with assigned courses, unavailable time slots, and preferred teaching times.
 */
public class Instructor {
    private String instructorId;
    private String name;
    private List<String> assignedCourses;
    private List<TimeSlot> unavailableSlots;
    private String preferredTimes;

    public Instructor(String instructorId, String name, String preferredTimes) {
        this.instructorId = instructorId;
        this.name = name;
        this.assignedCourses = new ArrayList<>();
        this.unavailableSlots = new ArrayList<>();
        this.preferredTimes = preferredTimes;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAssignedCourses() {
        return assignedCourses;
    }

    public void addAssignedCourse(String courseCode) {
        assignedCourses.add(courseCode);
    }

    public List<TimeSlot> getUnavailableSlots() {
        return unavailableSlots;
    }

    public void addUnavailableSlot(TimeSlot slot) {
        unavailableSlots.add(slot);
    }

    public String getPreferredTimes() {
        return preferredTimes;
    }

    public void setPreferredTimes(String preferredTimes) {
        this.preferredTimes = preferredTimes;
    }

    /**
     * Checks if the instructor is available at the given time slot (HC-07).
     */
    public boolean isAvailable(TimeSlot slot) {
        for (TimeSlot unavailable : unavailableSlots) {
            if (unavailable.overlapsWith(slot)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return instructorId + " - " + name;
    }
}


