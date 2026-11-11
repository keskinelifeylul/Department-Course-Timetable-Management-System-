import java.util.ArrayList;

public class Scheduler {
    private Timetable timetable;
    private ConstraintChecker checker;
    private ArrayList<Room> rooms;
    private ArrayList<SpecialRequest> requests;
    private ArrayList<String> log;

    public Scheduler(Timetable timetable, ConstraintChecker checker,
                     ArrayList<Room> rooms, ArrayList<SpecialRequest> requests) {
        this.timetable = timetable;
        this.checker = checker;
        this.rooms = rooms;
        this.requests = requests;
        this.log = new ArrayList<>();
    }

    public void generateTimetable() {
        // TODO: planned for V2
    }

    private void scheduleFixedSessions() {
        // TODO: planned for V3
    }

    private ArrayList<Session> sortSessions() {
        // TODO: planned for V2
        return new ArrayList<>();
    }

    private boolean scheduleSession(Session s) {
        // TODO: planned for V2
        return false;
    }

    private Room findRoom(Session s, TimeSlot slot) {
        // TODO: planned for V2
        return null;
    }

    public boolean hasSuitableRoom(Session s) {
        // TODO: planned for V2
        return false;
    }

    public ArrayList<String> getLog() {
        return log;
    }
}
