public class ReportGenerator {
    private Timetable timetable;

    public ReportGenerator(Timetable timetable) {
        this.timetable = timetable;
    }

    public void printYearTimetable(int year) {
        // TODO: planned for V2
    }

    public void printAllSessions() {
        // TODO: planned for V2
    }

    public void printUnscheduled() {
        // TODO: planned for V2
    }

    public String createEmailDraft(Session s, TimeSlot slot) {
        // TODO: planned for V3
        return "";
    }

    public boolean saveTimetable(String path) {
        // TODO: planned for V3
        return false;
    }

    public boolean saveText(String path, String text) {
        // TODO: planned for V3
        return false;
    }
}
