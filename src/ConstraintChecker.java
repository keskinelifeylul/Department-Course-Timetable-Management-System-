import java.util.ArrayList;

public class ConstraintChecker {
    private Timetable timetable;
    private ArrayList<SpecialRequest> requests;

    public ConstraintChecker(Timetable timetable, ArrayList<SpecialRequest> requests) {
        this.timetable = timetable;
        this.requests = requests;
    }

    public ArrayList<String> checkHard(Session s, TimeSlot slot, Room r) {
        ArrayList<String> violations = new ArrayList<>();
        if (s == null || slot == null || r == null) {
            return violations;
        }

        addIfViolation(violations, checkWorkingHours(s, slot));
        addIfViolation(violations, checkRoomType(s, r));
        addIfViolation(violations, checkRoomCapacity(s, r));
        addIfViolation(violations, checkInstructorConflict(s, slot));
        addIfViolation(violations, checkRoomConflict(s, slot, r));
        addIfViolation(violations, checkYearGroupConflict(s, slot));

        // HC-07..HC-09 planned for V3 (Code v5)
        addIfViolation(violations, checkInstructorAvailability(s, slot));
        addIfViolation(violations, checkFixedTime(s, slot));
        addIfViolation(violations, checkRoomAvailability(slot, r));

        return violations;
    }

    private void addIfViolation(ArrayList<String> list, String msg) {
        if (msg != null && !msg.isEmpty()) {
            list.add(msg);
        }
    }

    public ArrayList<String> checkSoft(Session s, TimeSlot slot) {
        // TODO: planned for V3 (Code v5)
        return new ArrayList<>();
    }

    private String checkInstructorConflict(Session s, TimeSlot slot) {
        Instructor instructor = s.getCourse().getInstructor();
        for (Session other : timetable.getScheduled()) {
            if (other == s) {
                continue;
            }
            if (other.getCourse().getInstructor().getInstructorId().equals(instructor.getInstructorId())
                    && other.getSlot().overlaps(slot)) {
                return "HC-01 Instructor conflict: " + instructor.getName()
                        + " already teaches " + other.getName() + " on " + other.getSlot();
            }
        }
        return null;
    }

    private String checkRoomConflict(Session s, TimeSlot slot, Room r) {
        for (Session other : timetable.getScheduled()) {
            if (other == s) {
                continue;
            }
            if (other.getRoom().getRoomId().equals(r.getRoomId())
                    && other.getSlot().overlaps(slot)) {
                return "HC-02 Room conflict: " + r.getRoomId()
                        + " is used by " + other.getName() + " on " + other.getSlot();
            }
        }
        return null;
    }

    private String checkYearGroupConflict(Session s, TimeSlot slot) {
        int year = s.getCourse().getYear();
        for (Session other : timetable.getScheduled()) {
            if (other == s) {
                continue;
            }
            if (other.getCourse().getYear() == year && other.getSlot().overlaps(slot)) {
                return "HC-03 Year-group conflict: Year " + year
                        + " already has " + other.getName() + " on " + other.getSlot();
            }
        }
        return null;
    }

    private String checkWorkingHours(Session s, TimeSlot slot) {
        if (!slot.isWithinWorkingHours()) {
            return String.format(
                    "HC-04 Working hours: %s (%d h) cannot start at %02d:00; sessions must be Mon-Fri 09:00-17:00",
                    s.getName(), s.getHours(), slot.getStartHour());
        }
        return null;
    }

    private String checkRoomCapacity(Session s, Room r) {
        if (!r.isSuitableFor(s) && r.getCapacity() < s.getCourse().getStudents()) {
            return "HC-05 Room capacity: " + r.getRoomId() + " (capacity " + r.getCapacity()
                    + ") is too small for " + s.getName() + " ("
                    + s.getCourse().getStudents() + " students)";
        }
        return null;
    }

    private String checkRoomType(Session s, Room r) {
        if (!r.isSuitableFor(s) && r.getType() != s.getRequiredRoomType()) {
            return "HC-06 Room type: " + s.getName() + " requires a "
                    + formatRoomType(s.getRequiredRoomType()) + ", but "
                    + r.getRoomId() + " is a " + formatRoomType(r.getType());
        }
        return null;
    }

    private String formatRoomType(RoomType type) {
        return type == RoomType.LABORATORY ? "Laboratory" : "Classroom";
    }

    private String checkInstructorAvailability(Session s, TimeSlot slot) {
        return null;
    }

    private String checkFixedTime(Session s, TimeSlot slot) {
        if (requests == null || s == null || slot == null) {
            return null;
        }
        return null;
    }

    private String checkRoomAvailability(TimeSlot slot, Room r) {
        return null;
    }
}
