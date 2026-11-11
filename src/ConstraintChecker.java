import java.util.ArrayList;

public class ConstraintChecker {
    private Timetable timetable;
    private ArrayList<SpecialRequest> requests;

    public ConstraintChecker(Timetable timetable, ArrayList<SpecialRequest> requests) {
        this.timetable = timetable;
        this.requests = requests;
    }

    public ArrayList<String> checkHard(Session s, TimeSlot slot, Room r) {
        // TODO: HC-01..HC-06 planned for V2, HC-07..HC-09 planned for V3
        return new ArrayList<>();
    }

    public ArrayList<String> checkSoft(Session s, TimeSlot slot) {
        // TODO: planned for V3
        return new ArrayList<>();
    }

    private String checkInstructorConflict(Session s, TimeSlot slot) {
        return null;
    }

    private String checkRoomConflict(Session s, TimeSlot slot, Room r) {
        return null;
    }

    private String checkYearGroupConflict(Session s, TimeSlot slot) {
        return null;
    }

    private String checkWorkingHours(Session s, TimeSlot slot) {
        return null;
    }

    private String checkRoomCapacity(Session s, Room r) {
        return null;
    }

    private String checkRoomType(Session s, Room r) {
        return null;
    }

    private String checkInstructorAvailability(Session s, TimeSlot slot) {
        return null;
    }

    private String checkFixedTime(Session s, TimeSlot slot) {
        return null;
    }

    private String checkRoomAvailability(TimeSlot slot, Room r) {
        return null;
    }
}
