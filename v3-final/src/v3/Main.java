package v3;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point and Console User Interface for the Department Course Timetable Management System (Final Release).
 */
public class Main {
    private static final String DEFAULT_DATA_DIR = resolveBaseDir("data");
    private static final String DEFAULT_OUTPUT_DIR = resolveBaseDir("output");

    private static String resolveBaseDir(String subDir) {
        if (new File(subDir).exists()) {
            return subDir;
        }
        if (new File("v3-final/" + subDir).exists() || new File("v3-final").exists()) {
            return "v3-final/" + subDir;
        }
        return subDir;
    }

    private DataLoader dataLoader;
    private Timetable timetable;
    private Scheduler scheduler;
    private Scanner scanner;

    public Main() {
        this.dataLoader = new DataLoader();
        this.timetable = new Timetable();
        this.scheduler = new Scheduler(dataLoader, timetable);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main app = new Main();
        if (args.length > 0 && "--demo".equalsIgnoreCase(args[0])) {
            app.runAutomatedDemo();
        } else {
            app.runInteractiveMenu();
        }
    }

    public void runInteractiveMenu() {
        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("Select an option: ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleLoadData();
                    break;
                case "2":
                    handleGenerateTimetable();
                    break;
                case "3":
                    handleViewByYearGroup();
                    break;
                case "4":
                    handleViewAllScheduled();
                    break;
                case "5":
                    handleViewUnscheduledAndWarnings();
                    break;
                case "6":
                    handleModifyOrRemoveAssignment();
                    break;
                case "7":
                    handleClassroomRequestEmail();
                    break;
                case "8":
                    handleSaveTimetable();
                    break;
                case "0":
                    System.out.println("Exiting Department Course Timetable Management System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number between 0 and 8.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n==================================================");
        System.out.println("  DEPARTMENT COURSE TIMETABLE MANAGEMENT SYSTEM");
        System.out.println("         Software Engineering Department");
        System.out.println("==================================================");
        System.out.println("  1. Load input data");
        System.out.println("  2. Generate timetable automatically");
        System.out.println("  3. View timetable by year group");
        System.out.println("  4. View all scheduled sessions");
        System.out.println("  5. View unscheduled sessions and warnings");
        System.out.println("  6. Modify / remove an assignment");
        System.out.println("  7. Generate classroom request email");
        System.out.println("  8. Save timetable to file");
        System.out.println("  0. Exit");
        System.out.println("--------------------------------------------------");
    }

    private void handleLoadData() {
        dataLoader.loadAllData(DEFAULT_DATA_DIR);
        waitForEnter();
    }

    private void handleGenerateTimetable() {
        if (!ensureDataLoaded()) {
            return;
        }
        scheduler.generateTimetable();
        waitForEnter();
    }

    private void handleViewByYearGroup() {
        if (!ensureTimetableGenerated()) {
            return;
        }
        System.out.print("Enter year group (1-4, or 0 for all years): ");
        String input = scanner.nextLine().trim();
        try {
            int year = Integer.parseInt(input);
            if (year == 0) {
                for (int y = 1; y <= 4; y++) {
                    timetable.printYearGroupTimetable(y, dataLoader.getInstructors());
                }
            } else if (year >= 1 && year <= 4) {
                timetable.printYearGroupTimetable(year, dataLoader.getInstructors());
            } else {
                System.out.println("Invalid year group. Please enter 1, 2, 3, or 4.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric input.");
        }
        waitForEnter();
    }

    private void handleViewAllScheduled() {
        if (!ensureTimetableGenerated()) {
            return;
        }
        timetable.printAllScheduledSessions(dataLoader.getInstructors());
        waitForEnter();
    }

    private void handleViewUnscheduledAndWarnings() {
        if (!ensureTimetableGenerated()) {
            return;
        }
        timetable.printUnscheduledAndWarnings();
        waitForEnter();
    }

    private void handleModifyOrRemoveAssignment() {
        if (!ensureTimetableGenerated()) {
            return;
        }
        System.out.println("\n=== MODIFY / REMOVE AN ASSIGNMENT ===");
        System.out.print("Session to modify (e.g. SENG203-L): ");
        String sessionId = scanner.nextLine().trim();
        Session session = timetable.findSessionById(sessionId);
        if (session == null) {
            System.out.println("Session \"" + sessionId + "\" not found.");
            waitForEnter();
            return;
        }

        if (session.isScheduled()) {
            System.out.printf("Current assignment: %s, %s%n",
                    session.getAssignedTimeSlot(), session.getAssignedRoom().getRoomId());
        } else {
            System.out.printf("Current assignment: UNSCHEDULED (%s)%n", session.getUnscheduledReason());
        }

        System.out.print("Action - (M)odify assignment or (R)emove assignment? [M/R]: ");
        String action = scanner.nextLine().trim();
        if ("R".equalsIgnoreCase(action)) {
            session.unassign("CONSTRAINT", "Manually removed by coordinator");
            System.out.println("Assignment removed for " + session.getSessionId() + ".");
            waitForEnter();
            return;
        }

        boolean trying = true;
        while (trying) {
            System.out.print("New day (Mon-Fri): ");
            String rawDay = scanner.nextLine().trim();
            String day = TimeSlot.normalizeDay(rawDay);

            System.out.print("New start time (e.g. 13:00): ");
            String rawTime = scanner.nextLine().trim();
            int startHour = TimeSlot.parseHour(rawTime);

            System.out.print("New room: ");
            String roomId = scanner.nextLine().trim().toUpperCase();

            if (startHour < 0) {
                System.out.println("  [X] Invalid time format.");
            } else {
                TimeSlot newSlot = new TimeSlot(day, startHour, startHour + session.getDurationHours());
                Room newRoom = dataLoader.getRooms().get(roomId);

                System.out.println("\nChecking hard constraints...");
                String hardError = scheduler.checkAllHardConstraints(session, newSlot, newRoom, dataLoader.getInstructors());
                if (hardError != null) {
                    System.out.println("  [X] " + hardError);
                    System.out.println("Modification REJECTED. The current assignment is kept.");
                } else {
                    System.out.println("  [OK] All hard constraints satisfied.");
                    List<String> softWarnings = scheduler.evaluateSoftConstraints(session, newSlot, dataLoader.getInstructors());
                    boolean apply = true;
                    if (!softWarnings.isEmpty()) {
                        for (String w : softWarnings) {
                            System.out.println("  [!] " + w);
                        }
                        System.out.print("Apply the change anyway? (y/n): ");
                        String confirm = scanner.nextLine().trim();
                        apply = "y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm);
                    }
                    if (apply) {
                        session.assign(newSlot, newRoom);
                        session.setPlacementNote("(manually modified)");
                        for (String w : softWarnings) {
                            timetable.addSoftWarning(w);
                        }
                        System.out.printf("Assignment updated: %s -> %s, %s%n",
                                session.getSessionId(), newSlot, newRoom.getRoomId());
                        trying = false;
                        break;
                    } else {
                        System.out.println("Modification cancelled. Current assignment kept.");
                        trying = false;
                        break;
                    }
                }
            }

            System.out.print("\nTry another option? (y/n): ");
            String retry = scanner.nextLine().trim();
            if (!"y".equalsIgnoreCase(retry) && !"yes".equalsIgnoreCase(retry)) {
                trying = false;
            }
        }
        waitForEnter();
    }

    private void handleClassroomRequestEmail() {
        if (!ensureTimetableGenerated()) {
            return;
        }
        System.out.println("\n=== CLASSROOM REQUEST ===");
        List<Session> noRoomSessions = timetable.getRoomShortageSessions();
        if (noRoomSessions.isEmpty()) {
            System.out.println("There are currently no sessions left unscheduled due to lack of a suitable room.");
            waitForEnter();
            return;
        }

        System.out.println("Sessions without a suitable room:");
        for (int i = 0; i < noRoomSessions.size(); i++) {
            Session s = noRoomSessions.get(i);
            System.out.printf("  %d. %-10s (Year %d, %d students, %s, %d h)%n",
                    i + 1, s.getSessionId(), s.getYearGroup(), s.getStudentCount(),
                    s.getRequiredRoomType(), s.getDurationHours());
        }

        System.out.print("Select a session (1-" + noRoomSessions.size() + "): ");
        String input = scanner.nextLine().trim();
        int idx;
        try {
            idx = Integer.parseInt(input) - 1;
            if (idx < 0 || idx >= noRoomSessions.size()) {
                System.out.println("Invalid selection.");
                waitForEnter();
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric selection.");
            waitForEnter();
            return;
        }

        Session selected = noRoomSessions.get(idx);
        TimeSlot suggested = scheduler.findSuggestedTimeForRoomRequest(selected);
        System.out.printf("Suggested time (year group and instructor both free): %s %s%n%n",
                TimeSlot.getFullDayName(suggested.getDay()), suggested.formatHours());

        String draft = scheduler.generateClassroomRequestEmail(selected, suggested);
        System.out.println("------------------- EMAIL DRAFT -------------------");
        System.out.print(draft);
        System.out.println("---------------------------------------------------");

        System.out.print("Save this email to a file? (y/n): ");
        String saveAns = scanner.nextLine().trim();
        if ("y".equalsIgnoreCase(saveAns) || "yes".equalsIgnoreCase(saveAns)) {
            try {
                String savedPath = scheduler.saveClassroomRequestEmail(selected, draft, DEFAULT_OUTPUT_DIR);
                System.out.println("Saved to " + savedPath);
            } catch (IOException e) {
                System.out.println("Error saving classroom request email: " + e.getMessage());
            }
        }
        waitForEnter();
    }

    private void handleSaveTimetable() {
        if (!ensureTimetableGenerated()) {
            return;
        }
        try {
            timetable.saveToFile(DEFAULT_OUTPUT_DIR, dataLoader.getInstructors());
            System.out.println("Timetable saved to " + DEFAULT_OUTPUT_DIR + "/timetable.txt and " + DEFAULT_OUTPUT_DIR + "/timetable.csv");
        } catch (IOException e) {
            System.out.println("Failed to save timetable: " + e.getMessage());
        }
        waitForEnter();
    }

    private boolean ensureDataLoaded() {
        if (!dataLoader.isDataLoaded()) {
            System.out.println("Input data is not loaded yet. Loading from " + DEFAULT_DATA_DIR + "...");
            dataLoader.loadAllData(DEFAULT_DATA_DIR);
        }
        return dataLoader.isDataLoaded();
    }

    private boolean ensureTimetableGenerated() {
        if (timetable.getAllSessions().isEmpty()) {
            System.out.println("Timetable has not been generated yet. Please select Option 2 first.");
            waitForEnter();
            return false;
        }
        return true;
    }

    private void waitForEnter() {
        System.out.print("\nPress Enter to return to the main menu...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    public void runAutomatedDemo() {
        printMainMenu();
        System.out.println("Select an option: 1");
        dataLoader.loadAllData(DEFAULT_DATA_DIR);

        System.out.println("\nSelect an option: 2");
        scheduler.generateTimetable();

        System.out.println("\nSelect an option: 3 (Year 2)");
        timetable.printYearGroupTimetable(2, dataLoader.getInstructors());

        System.out.println("\nSelect an option: 4");
        timetable.printAllScheduledSessions(dataLoader.getInstructors());

        System.out.println("\nSelect an option: 5");
        timetable.printUnscheduledAndWarnings();
    }
}

