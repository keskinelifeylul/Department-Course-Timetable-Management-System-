import java.util.ArrayList;

public class ReportGenerator {
    private Timetable timetable;

    public ReportGenerator(Timetable timetable) {
        this.timetable = timetable;
    }

    public void printYearTimetable(int year) {
        System.out.println();
        System.out.println("=== WEEKLY TIMETABLE - YEAR " + year + " ===");
        String border = "+-------------+----------------+----------------+----------------+----------------+----------------+";
        System.out.println(border);
        System.out.printf("| %-11s | %-14s | %-14s | %-14s | %-14s | %-14s |%n",
                "Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        System.out.println(border);

        ArrayList<Session> yearSessions = timetable.getByYear(year);
        for (int hour = TimeSlot.FIRST_HOUR; hour < TimeSlot.LAST_HOUR; hour++) {
            String timeLabel = String.format("%02d:00-%02d:00", hour, hour + 1);
            String[] cells = new String[TimeSlot.DAY_NAMES.length];
            for (int day = 0; day < TimeSlot.DAY_NAMES.length; day++) {
                cells[day] = "";
                for (Session s : yearSessions) {
                    if (s.isScheduled()) {
                        TimeSlot ts = s.getSlot();
                        if (ts.getDay() == day && hour >= ts.getStartHour() && hour < ts.getEndHour()) {
                            cells[day] = s.getName() + " " + s.getRoom().getRoomId();
                            break;
                        }
                    }
                }
            }
            System.out.printf("| %-11s | %-14s | %-14s | %-14s | %-14s | %-14s |%n",
                    timeLabel, cells[0], cells[1], cells[2], cells[3], cells[4]);
        }
        System.out.println(border);
        System.out.println("T = Theory, L = Laboratory");
        System.out.println();

        System.out.println("Session details:");
        boolean anyScheduled = false;
        for (Session s : yearSessions) {
            if (s.isScheduled()) {
                anyScheduled = true;
                System.out.printf("  %-10s %-36s %-14s %-16s %s%n",
                        s.getName(),
                        s.getCourse().getTitle(),
                        s.getCourse().getInstructor().getName(),
                        s.getSlot(),
                        s.getRoom().getRoomId());
            }
        }
        if (!anyScheduled) {
            System.out.println("  (No scheduled sessions for Year " + year + ")");
        }

        ArrayList<Session> unscheduledForYear = new ArrayList<>();
        for (Session s : yearSessions) {
            if (!s.isScheduled()) {
                unscheduledForYear.add(s);
            }
        }
        if (!unscheduledForYear.isEmpty()) {
            System.out.println();
            System.out.println("Unscheduled sessions for Year " + year + ":");
            for (Session s : unscheduledForYear) {
                String pType = formatProblemType(s.getProblemType());
                String reason = (s.getProblemReason() != null) ? s.getProblemReason() : "not scheduled";
                System.out.printf("  %-10s (%d h, %d students) [%s] %s%n",
                        s.getName(), s.getHours(), s.getCourse().getStudents(), pType, reason);
            }
        }
    }

    public void printAllSessions() {
        System.out.println();
        System.out.println("=== ALL SCHEDULED SESSIONS ===");
        ArrayList<Session> scheduled = timetable.getScheduled();
        if (scheduled.isEmpty()) {
            System.out.println("No sessions are currently scheduled.");
            return;
        }
        System.out.printf("%-10s %-36s %-5s %-14s %-16s %-6s%n",
                "Session", "Course Title", "Year", "Instructor", "Day & Time", "Room");
        System.out.println("--------------------------------------------------------------------------------------------");
        for (Session s : scheduled) {
            System.out.printf("%-10s %-36s %-5d %-14s %-16s %-6s%n",
                    s.getName(),
                    s.getCourse().getTitle(),
                    s.getCourse().getYear(),
                    s.getCourse().getInstructor().getName(),
                    s.getSlot(),
                    s.getRoom().getRoomId());
        }
    }

    public void printUnscheduled() {
        System.out.println();
        System.out.println("=== UNSCHEDULED SESSIONS ===");
        ArrayList<Session> unscheduled = timetable.getUnscheduled();
        if (unscheduled.isEmpty()) {
            System.out.println("All sessions are scheduled.");
        } else {
            System.out.printf("%-3s %-10s %-5s %-6s %-9s %-13s %s%n",
                    "#", "Session", "Year", "Hours", "Students", "Problem type", "Reason");
            System.out.println("--------------------------------------------------------------------------------------------");
            int idx = 1;
            boolean hasNoRoom = false;
            boolean hasConstraint = false;
            for (Session s : unscheduled) {
                String pType = formatProblemType(s.getProblemType());
                String reason = (s.getProblemReason() != null) ? s.getProblemReason() : "not scheduled";
                System.out.printf("%-3d %-10s %-5d %-6d %-9d %-13s %s%n",
                        idx++,
                        s.getName(),
                        s.getCourse().getYear(),
                        s.getHours(),
                        s.getCourse().getStudents(),
                        pType,
                        reason);
                if (s.getProblemType() == ProblemType.NO_ROOM) {
                    hasNoRoom = true;
                } else {
                    hasConstraint = true;
                }
            }
            System.out.println();
            if (hasNoRoom) {
                System.out.println("-> Sessions marked NO ROOM require a classroom request email (option 7, planned for V3).");
            }
            if (hasConstraint) {
                System.out.println("-> Sessions marked CONSTRAINT must be resolved by the coordinator.");
            }
        }

        System.out.println();
        System.out.println("=== SOFT-CONSTRAINT WARNINGS ===");
        if (timetable.getWarnings().isEmpty()) {
            System.out.println("No soft-constraint warnings.");
        } else {
            for (String w : timetable.getWarnings()) {
                System.out.println("  [WARN] " + w);
            }
        }
    }

    private String formatProblemType(ProblemType type) {
        if (type == ProblemType.NO_ROOM) {
            return "NO ROOM";
        }
        if (type == ProblemType.CONSTRAINT) {
            return "CONSTRAINT";
        }
        return "UNSCHEDULED";
    }

    public String createEmailDraft(Session s, TimeSlot slot) {
        // TODO: planned for V3 (Code v6)
        return "";
    }

    public boolean saveTimetable(String path) {
        // TODO: planned for V3 (Code v6)
        return false;
    }

    public boolean saveText(String path, String text) {
        // TODO: planned for V3 (Code v6)
        return false;
    }
}
