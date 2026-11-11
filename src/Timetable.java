import java.util.ArrayList;

public class Timetable {
    private ArrayList<Session> sessions;
    private ArrayList<String> warnings;

    public Timetable() {
        this.sessions = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public ArrayList<String> getWarnings() {
        return warnings;
    }

    public void addSession(Session s) {
        if (s != null) {
            sessions.add(s);
        }
    }

    public ArrayList<Session> getScheduled() {
        ArrayList<Session> result = new ArrayList<>();
        for (Session s : sessions) {
            if (s.isScheduled()) {
                result.add(s);
            }
        }
        return result;
    }

    public ArrayList<Session> getUnscheduled() {
        ArrayList<Session> result = new ArrayList<>();
        for (Session s : sessions) {
            if (!s.isScheduled()) {
                result.add(s);
            }
        }
        return result;
    }

    public ArrayList<Session> getByYear(int year) {
        ArrayList<Session> result = new ArrayList<>();
        for (Session s : sessions) {
            if (s.getCourse().getYear() == year) {
                result.add(s);
            }
        }
        return result;
    }

    public Session findSession(String name) {
        if (name == null) {
            return null;
        }
        for (Session s : sessions) {
            if (s.getName().equalsIgnoreCase(name.trim())) {
                return s;
            }
        }
        return null;
    }

    public void addWarning(String w) {
        if (w != null && !w.isEmpty()) {
            warnings.add(w);
        }
    }

    public void clearAssignments() {
        for (Session s : sessions) {
            s.unassign();
        }
        warnings.clear();
    }
}
