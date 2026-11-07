package v3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages the generated weekly schedule, including scheduled sessions,
 * unscheduled sessions, soft-constraint warnings, grid formatting, and file export.
 */
public class Timetable {
    private List<Session> allSessions = new ArrayList<>();
    private List<String> softWarnings = new ArrayList<>();

    public void clear() {
        allSessions.clear();
        softWarnings.clear();
    }

    public List<Session> getAllSessions() {
        return allSessions;
    }

    public void setAllSessions(List<Session> sessions) {
        this.allSessions = sessions;
    }

    public List<String> getSoftWarnings() {
        return softWarnings;
    }

    public void addSoftWarning(String warning) {
        if (!softWarnings.contains(warning)) {
            softWarnings.add(warning);
        }
    }

    public List<Session> getScheduledSessions() {
        List<Session> list = new ArrayList<>();
        for (Session s : allSessions) {
            if (s.isScheduled()) {
                list.add(s);
            }
        }
        return list;
    }

    public List<Session> getUnscheduledSessions() {
        List<Session> list = new ArrayList<>();
        for (Session s : allSessions) {
            if (!s.isScheduled()) {
                list.add(s);
            }
        }
        return list;
    }

    public List<Session> getRoomShortageSessions() {
        List<Session> list = new ArrayList<>();
        for (Session s : allSessions) {
            if (!s.isScheduled() && "NO ROOM".equalsIgnoreCase(s.getProblemType())) {
                list.add(s);
            }
        }
        return list;
    }

    public Session findSessionById(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        for (Session s : allSessions) {
            if (s.getSessionId().equalsIgnoreCase(sessionId.trim())) {
                return s;
            }
        }
        return null;
    }

    public void printYearGroupTimetable(int yearGroup, Map<String, Instructor> instructors) {
        System.out.print(buildYearGroupTimetableText(yearGroup, instructors));
    }

    public String buildYearGroupTimetableText(int yearGroup, Map<String, Instructor> instructors) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%n=== WEEKLY TIMETABLE - YEAR %d ===%n", yearGroup));
        String border = "+-------------+----------------+----------------+----------------+----------------+----------------+\n";
        sb.append(border);
        sb.append(String.format("| %-11s | %-14s | %-14s | %-14s | %-14s | %-14s |%n",
                "Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
        sb.append(border);

        for (int hour = TimeSlot.DAY_START_HOUR; hour < TimeSlot.DAY_END_HOUR; hour++) {
            String timeLabel = String.format("%02d:00-%02d:00", hour, hour + 1);
            String[] cells = new String[5];
            for (int d = 0; d < 5; d++) {
                String day = TimeSlot.WORKING_DAYS.get(d);
                cells[d] = "";
                for (Session s : getScheduledSessions()) {
                    if (s.getYearGroup() == yearGroup) {
                        TimeSlot ts = s.getAssignedTimeSlot();
                        if (ts.getDay().equalsIgnoreCase(day) && hour >= ts.getStartHour() && hour < ts.getEndHour()) {
                            cells[d] = s.getSessionId() + " " + s.getAssignedRoom().getRoomId();
                            break;
                        }
                    }
                }
            }
            sb.append(String.format("| %-11s | %-14s | %-14s | %-14s | %-14s | %-14s |%n",
                    timeLabel, cells[0], cells[1], cells[2], cells[3], cells[4]));
        }
        sb.append(border);
        sb.append("T = Theory, L = Laboratory\n\n");

        sb.append("Session details:\n");
        boolean anyScheduled = false;
        for (Session s : getScheduledSessions()) {
            if (s.getYearGroup() == yearGroup) {
                anyScheduled = true;
                Instructor inst = instructors.get(s.getInstructorId());
                String instName = (inst != null) ? inst.getName() : s.getInstructorId();
                sb.append(String.format("  %-10s %-30s %-12s %-15s %s%n",
                        s.getSessionId(),
                        s.getCourse().getTitle(),
                        instName,
                        s.getAssignedTimeSlot(),
                        s.getAssignedRoom().getRoomId()));
            }
        }
        if (!anyScheduled) {
            sb.append("  (No scheduled sessions for this year group)\n");
        }

        List<String> unscheduledForYear = new ArrayList<>();
        for (Session s : getUnscheduledSessions()) {
            if (s.getYearGroup() == yearGroup) {
                unscheduledForYear.add(String.format("%s (%d h)", s.getSessionId(), s.getDurationHours()));
            }
        }
        if (!unscheduledForYear.isEmpty()) {
            sb.append(String.format("%nNot scheduled for Year %d: %s -> see option 5%n",
                    yearGroup, String.join(", ", unscheduledForYear)));
        }
        return sb.toString();
    }

    public void printAllScheduledSessions(Map<String, Instructor> instructors) {
        System.out.println("\n=== ALL SCHEDULED SESSIONS ===");
        List<Session> scheduled = getScheduledSessions();
        if (scheduled.isEmpty()) {
            System.out.println("No sessions are currently scheduled.");
            return;
        }
        System.out.printf("%-10s %-30s %-6s %-12s %-16s %-8s%n",
                "Session", "Course Title", "Year", "Instructor", "Day & Time", "Room");
        System.out.println("--------------------------------------------------------------------------------------");
        for (Session s : scheduled) {
            Instructor inst = instructors.get(s.getInstructorId());
            String instName = (inst != null) ? inst.getName() : s.getInstructorId();
            System.out.printf("%-10s %-30s %-6d %-12s %-16s %-8s%n",
                    s.getSessionId(),
                    s.getCourse().getTitle(),
                    s.getYearGroup(),
                    instName,
                    s.getAssignedTimeSlot(),
                    s.getAssignedRoom().getRoomId());
        }
    }

    public void printUnscheduledAndWarnings() {
        System.out.println("\n=== UNSCHEDULED SESSIONS ===");
        List<Session> unscheduled = getUnscheduledSessions();
        if (unscheduled.isEmpty()) {
            System.out.println("All sessions have been successfully scheduled!");
        } else {
            System.out.printf("%-3s %-10s %-6s %-6s %-9s %-14s %s%n",
                    "#", "Session", "Year", "Hours", "Students", "Problem type", "Reason");
            int idx = 1;
            boolean hasNoRoom = false;
            boolean hasConstraint = false;
            for (Session s : unscheduled) {
                System.out.printf("%-3d %-10s %-6d %-6d %-9d %-14s %s%n",
                        idx++,
                        s.getSessionId(),
                        s.getYearGroup(),
                        s.getDurationHours(),
                        s.getStudentCount(),
                        s.getProblemType(),
                        s.getUnscheduledReason());
                if ("NO ROOM".equalsIgnoreCase(s.getProblemType())) {
                    hasNoRoom = true;
                } else {
                    hasConstraint = true;
                }
            }
            System.out.println();
            if (hasNoRoom) {
                System.out.println("-> Sessions with NO ROOM can be solved with a classroom request (option 7).");
            }
            if (hasConstraint) {
                System.out.println("-> Sessions with CONSTRAINT problems must be resolved by the coordinator (change the request or contact the instructor).");
            }
        }

        System.out.println("\n=== SOFT-CONSTRAINT WARNINGS ===");
        if (softWarnings.isEmpty()) {
            System.out.println("No soft-constraint warnings.");
        } else {
            for (String w : softWarnings) {
                System.out.println("- " + w);
            }
        }
    }

    public void saveToFile(String outputDir, Map<String, Instructor> instructors) throws IOException {
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File txtFile = new File(dir, "timetable.txt");
        try (PrintWriter out = new PrintWriter(new FileWriter(txtFile))) {
            out.println("==================================================");
            out.println("  DEPARTMENT COURSE TIMETABLE MANAGEMENT SYSTEM");
            out.println("==================================================");
            for (int y = 1; y <= 4; y++) {
                out.print(buildYearGroupTimetableText(y, instructors));
            }
            out.println("\n=== UNSCHEDULED SESSIONS ===");
            for (Session s : getUnscheduledSessions()) {
                out.printf("%-10s (Year %d, %dh, %d students) [%s] -> %s%n",
                        s.getSessionId(), s.getYearGroup(), s.getDurationHours(),
                        s.getStudentCount(), s.getProblemType(), s.getUnscheduledReason());
            }
            out.println("\n=== SOFT-CONSTRAINT WARNINGS ===");
            for (String w : softWarnings) {
                out.println("- " + w);
            }
        }

        File csvFile = new File(dir, "timetable.csv");
        try (PrintWriter csv = new PrintWriter(new FileWriter(csvFile))) {
            csv.println("SessionId,CourseCode,CourseTitle,YearGroup,Instructor,Status,Day,Time,Room,Notes");
            for (Session s : allSessions) {
                Instructor inst = instructors.get(s.getInstructorId());
                String instName = (inst != null) ? inst.getName() : s.getInstructorId();
                if (s.isScheduled()) {
                    csv.printf("%s,%s,\"%s\",%d,\"%s\",SCHEDULED,%s,%s,%s,\"%s\"%n",
                            s.getSessionId(),
                            s.getCourse().getCourseCode(),
                            s.getCourse().getTitle(),
                            s.getYearGroup(),
                            instName,
                            s.getAssignedTimeSlot().getDay(),
                            s.getAssignedTimeSlot().formatHours(),
                            s.getAssignedRoom().getRoomId(),
                            s.getPlacementNote());
                } else {
                    csv.printf("%s,%s,\"%s\",%d,\"%s\",UNSCHEDULED,NONE,NONE,NONE,\"%s: %s\"%n",
                            s.getSessionId(),
                            s.getCourse().getCourseCode(),
                            s.getCourse().getTitle(),
                            s.getYearGroup(),
                            instName,
                            s.getProblemType(),
                            s.getUnscheduledReason());
                }
            }
        }
    }
}

