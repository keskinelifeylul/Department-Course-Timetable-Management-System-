package v2;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instructor with assigned courses, unavailable time slots, and teaching preferences.
 */
public class Instructor {
    private String instructorId;
    private String name;
    private List<String> assignedCourseCodes;
    private List<TimeSlot> unavailableSlots;
    private String preferredTime;

    public Instructor(String instructorId, String name, String preferredTime) {
        this.instructorId = instructorId.trim();
        this.name = name.trim();
        this.assignedCourseCodes = new ArrayList<>();
        this.unavailableSlots = new ArrayList<>();
        this.preferredTime = (preferredTime == null || preferredTime.trim().isEmpty())
                ? "NONE" : preferredTime.trim().toUpperCase();
    }

    public String getInstructorId() {
        return instructorId;
    }

    public String getName() {
        return name;
    }

    public List<String> getAssignedCourseCodes() {
        return assignedCourseCodes;
    }

    public void addAssignedCourse(String courseCode) {
        if (!assignedCourseCodes.contains(courseCode)) {
            assignedCourseCodes.add(courseCode);
        }
    }

    public List<TimeSlot> getUnavailableSlots() {
        return unavailableSlots;
    }

    public void addUnavailableSlot(TimeSlot slot) {
        if (slot != null) {
            unavailableSlots.add(slot);
        }
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public boolean isAvailable(TimeSlot candidate) {
        for (TimeSlot unavail : unavailableSlots) {
            if (unavail.overlapsWith(candidate)) {
                return false;
            }
        }
        return true;
    }

    public boolean satisfiesPreference(TimeSlot candidate) {
        if ("MORNING".equalsIgnoreCase(preferredTime)) {
            return candidate.isMorning();
        } else if ("AFTERNOON".equalsIgnoreCase(preferredTime)) {
            return candidate.isAfternoon();
        }
        return true;
    }

    @Override
    public String toString() {
        return instructorId + " - " + name;
    }
}

