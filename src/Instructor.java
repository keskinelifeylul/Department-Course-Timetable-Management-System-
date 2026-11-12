import java.util.ArrayList;

public class Instructor {
    private String instructorId;
    private String name;
    private ArrayList<TimeSlot> unavailableSlots;
    private String preferredTime;
    private ArrayList<Course> courses;

    public Instructor(String instructorId, String name, String preferredTime) {
        this.instructorId = instructorId;
        this.name = name;
        this.preferredTime = preferredTime;
        this.unavailableSlots = new ArrayList<>();
        this.courses = new ArrayList<>();
    }

    public String getInstructorId() {
        return instructorId;
    }

    public String getName() {
        return name;
    }

    public ArrayList<TimeSlot> getUnavailableSlots() {
        return unavailableSlots;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public boolean isAvailable(TimeSlot slot) {
        if (slot == null) {
            return false;
        }
        for (TimeSlot unavailable : unavailableSlots) {
            if (unavailable.overlaps(slot)) {
                return false;
            }
        }
        return true;
    }

    public boolean prefers(TimeSlot slot) {
        // TODO: planned for V3 (Code v5)
        return true;
    }

    public void addUnavailableSlot(TimeSlot slot) {
        if (slot != null && !unavailableSlots.contains(slot)) {
            unavailableSlots.add(slot);
        }
    }

    public void addCourse(Course course) {
        if (course != null && !courses.contains(course)) {
            courses.add(course);
        }
    }

    @Override
    public String toString() {
        return instructorId + " - " + name;
    }
}
