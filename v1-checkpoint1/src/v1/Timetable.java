package v1;

import java.util.ArrayList;
import java.util.List;

/**
 * Timetable.java (Code v1 - Project Skeleton)
 * Stores scheduled and unscheduled sessions and provides methods to view and export the timetable.
 */
public class Timetable {
    private List<String> scheduledSessions;
    private List<String> unscheduledSessions;
    private List<String> warnings;

    public Timetable() {
        this.scheduledSessions = new ArrayList<>();
        this.unscheduledSessions = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public List<String> getScheduledSessions() {
        return scheduledSessions;
    }

    public List<String> getUnscheduledSessions() {
        return unscheduledSessions;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void addScheduledSession(String record) {
        scheduledSessions.add(record);
    }

    public void addUnscheduledSession(String record) {
        unscheduledSessions.add(record);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void displayTimetableByYear(int yearGroup) {
        System.out.println("\n=== WEEKLY TIMETABLE - YEAR " + yearGroup + " ===");
        System.out.println("[Code v1 Skeleton] Timetable grid display for Year " + yearGroup + " is initialized.");
    }

    public void displayAllScheduledSessions() {
        System.out.println("\n=== ALL SCHEDULED SESSIONS ===");
        if (scheduledSessions.isEmpty()) {
            System.out.println("[Code v1 Skeleton] No sessions scheduled yet.");
        } else {
            for (String s : scheduledSessions) {
                System.out.println("  " + s);
            }
        }
    }

    public void displayUnscheduledAndWarnings() {
        System.out.println("\n=== UNSCHEDULED SESSIONS & WARNINGS ===");
        if (unscheduledSessions.isEmpty()) {
            System.out.println("[Code v1 Skeleton] No unscheduled sessions recorded yet.");
        } else {
            for (String u : unscheduledSessions) {
                System.out.println("  " + u);
            }
        }
    }

    public void saveToFile(String filePath) {
        System.out.println("\n[Code v1 Skeleton] Saving timetable to " + filePath + "...");
    }
}


