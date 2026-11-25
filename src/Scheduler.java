import java.util.ArrayList;

public class Scheduler {
    private Timetable timetable;
    private ConstraintChecker checker;
    private ArrayList<Room> rooms;
    private ArrayList<SpecialRequest> requests;
    private ArrayList<String> log;
    private int lastRejectedCount;
    private String lastFailureReason;
    private boolean lastFailureOnlyRoomBusy;

    public Scheduler(Timetable timetable, ConstraintChecker checker,
                     ArrayList<Room> rooms, ArrayList<SpecialRequest> requests) {
        this.timetable = timetable;
        this.checker = checker;
        this.rooms = rooms;
        this.requests = requests;
        this.log = new ArrayList<>();
    }

    public void generateTimetable() {
        timetable.clearAssignments();
        log.clear();

        // Step 1/2: Fixed-time requests (full placement planned for V3 / Code v5;
        // in V2 we mark mandatory FIXED_TIME and SPECIFIC_ROOM sessions as planned for V3
        // so Checkpoint 2 does not place them at wrong times/rooms).
        scheduleFixedSessions();

        // Step 2/2: Schedule remaining sessions in greedy order
        ArrayList<Session> ordered = sortSessions();
        for (Session s : ordered) {
            if (s.isScheduled() || s.getProblemType() != null) {
                continue;
            }
            SpecialRequest mandatorySpecificRoom = findMandatoryRequest(s, RequestType.SPECIFIC_ROOM);
            if (mandatorySpecificRoom != null) {
                String reason = "HC-08: mandatory SPECIFIC_ROOM request ("
                        + mandatorySpecificRoom.getRoomId() + ") planned for V3";
                s.markUnscheduled(ProblemType.CONSTRAINT, reason);
                log.add(String.format("[FAIL] %-10s %s", s.getName(), reason));
                continue;
            }
            scheduleSession(s);
        }
    }

    private void scheduleFixedSessions() {
        // Full fixed-time placement (HC-08) is planned for V3 (Code v5).
        // Mark mandatory FIXED_TIME sessions so they are not placed at arbitrary slots in V2.
        for (SpecialRequest req : requests) {
            if (req.isMandatory() && req.getType() == RequestType.FIXED_TIME) {
                for (Session s : timetable.getSessions()) {
                    if (req.appliesTo(s) && !s.isScheduled() && s.getProblemType() == null) {
                        String reason = "HC-08: mandatory FIXED_TIME request ("
                                + req.getSlot() + ") planned for V3";
                        s.markUnscheduled(ProblemType.CONSTRAINT, reason);
                        log.add(String.format("[FAIL] %-10s %s", s.getName(), reason));
                    }
                }
            }
        }
    }

    private SpecialRequest findMandatoryRequest(Session s, RequestType type) {
        for (SpecialRequest req : requests) {
            if (req.isMandatory() && req.getType() == type && req.appliesTo(s)) {
                return req;
            }
        }
        return null;
    }

    private ArrayList<Session> sortSessions() {
        ArrayList<Session> sorted = new ArrayList<>(timetable.getSessions());
        for (int i = 0; i < sorted.size() - 1; i++) {
            for (int j = i + 1; j < sorted.size(); j++) {
                if (compareSessions(sorted.get(i), sorted.get(j)) > 0) {
                    Session temp = sorted.get(i);
                    sorted.set(i, sorted.get(j));
                    sorted.set(j, temp);
                }
            }
        }
        return sorted;
    }

    private int compareSessions(Session a, Session b) {
        int studentCmp = Integer.compare(
                b.getCourse().getStudents(), a.getCourse().getStudents());
        if (studentCmp != 0) {
            return studentCmp;
        }
        if (a.getCourse().getCode().equalsIgnoreCase(b.getCourse().getCode())) {
            if (a.getType() != b.getType()) {
                return a.getType() == SessionType.THEORY ? -1 : 1;
            }
        }
        int hourCmp = Integer.compare(b.getHours(), a.getHours());
        if (hourCmp != 0) {
            return hourCmp;
        }
        if (a.getType() != b.getType()) {
            return a.getType() == SessionType.THEORY ? -1 : 1;
        }
        return a.getName().compareToIgnoreCase(b.getName());
    }

    private boolean scheduleSession(Session s) {
        if (!hasSuitableRoom(s)) {
            String kind = (s.getRequiredRoomType() == RoomType.LABORATORY)
                    ? "laboratory"
                    : "classroom";
            String reason = "HC-05: no " + kind + " with capacity >= " + s.getCourse().getStudents();
            s.markUnscheduled(ProblemType.NO_ROOM, reason);
            log.add(String.format("[FAIL] %-10s %s", s.getName(), reason));
            return false;
        }

        int totalRejected = 0;
        String lastReason = null;
        boolean allFailuresRoomOnly = true;

        int maxStartHour = TimeSlot.LAST_HOUR - s.getHours();
        for (int day = 0; day < TimeSlot.DAY_NAMES.length; day++) {
            for (int startHour = TimeSlot.FIRST_HOUR; startHour <= maxStartHour; startHour++) {
                TimeSlot candidateSlot = new TimeSlot(day, startHour, s.getHours());
                Room assignedRoom = findRoom(s, candidateSlot);
                if (assignedRoom != null) {
                    totalRejected += lastRejectedCount;
                    s.assign(candidateSlot, assignedRoom);
                    String suffix = (totalRejected > 0)
                            ? " (after " + totalRejected + " rejected attempt"
                                    + (totalRejected == 1 ? "" : "s") + ")"
                            : "";
                    log.add(String.format("[OK]   %-10s -> %s, Room %s%s",
                            s.getName(), candidateSlot, assignedRoom.getRoomId(), suffix));
                    return true;
                }
                totalRejected += lastRejectedCount;
                if (lastFailureReason != null) {
                    lastReason = lastFailureReason;
                }
                if (!lastFailureOnlyRoomBusy) {
                    allFailuresRoomOnly = false;
                }
            }
        }

        if (allFailuresRoomOnly) {
            String reason = "HC-02: all suitable rooms are busy in all free slots";
            s.markUnscheduled(ProblemType.NO_ROOM, reason);
            log.add(String.format("[FAIL] %-10s %s", s.getName(), reason));
        } else {
            String reason = (lastReason != null)
                    ? lastReason
                    : "HC-01/HC-03: no conflict-free time slot available";
            s.markUnscheduled(ProblemType.CONSTRAINT, reason);
            log.add(String.format("[FAIL] %-10s %s", s.getName(), reason));
        }
        return false;
    }

    private Room findRoom(Session s, TimeSlot slot) {
        lastRejectedCount = 0;
        lastFailureReason = null;
        lastFailureOnlyRoomBusy = true;

        ArrayList<Room> suitableRooms = getSuitableRoomsSorted(s);
        for (Room room : suitableRooms) {
            ArrayList<String> violations = checker.checkHard(s, slot, room);
            if (violations.isEmpty()) {
                return room;
            }
            lastRejectedCount++;
            lastFailureReason = violations.get(0);
            for (String v : violations) {
                if (!v.startsWith("HC-02") && !v.startsWith("HC-09")) {
                    lastFailureOnlyRoomBusy = false;
                }
            }
        }
        return null;
    }

    private ArrayList<Room> getSuitableRoomsSorted(Session s) {
        ArrayList<Room> suitable = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isSuitableFor(s)) {
                suitable.add(r);
            }
        }
        for (int i = 0; i < suitable.size() - 1; i++) {
            for (int j = i + 1; j < suitable.size(); j++) {
                Room a = suitable.get(i);
                Room b = suitable.get(j);
                int capCmp = Integer.compare(a.getCapacity(), b.getCapacity());
                if (capCmp > 0 || (capCmp == 0 && a.getRoomId().compareToIgnoreCase(b.getRoomId()) > 0)) {
                    suitable.set(i, b);
                    suitable.set(j, a);
                }
            }
        }
        return suitable;
    }

    public boolean hasSuitableRoom(Session s) {
        if (s == null) {
            return false;
        }
        for (Room r : rooms) {
            if (r.isSuitableFor(s)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getLog() {
        return log;
    }
}
