package v2;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main.java (Checkpoint 2 / Code v2-v4 - Core Implementation & Automatic Scheduling)
 * Implements Screens 1, 2, 3, 4, and 5 as required by Section 8.2 of the assignment.
 */
public class Main {
    private static final String DEFAULT_DATA_DIR = resolveBaseDir("data");
    private static final String DEFAULT_OUTPUT_DIR = resolveBaseDir("output");

    private static String resolveBaseDir(String subDir) {
        if (new File(subDir).exists()) {
            return subDir;
        }
        if (new File("v2-checkpoint2/" + subDir).exists() || new File("v2-checkpoint2").exists()) {
            return "v2-checkpoint2/" + subDir;
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
        app.runInteractiveMenu();
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
                    dataLoader.loadAllData(DEFAULT_DATA_DIR);
                    waitForEnter();
                    break;
                case "2":
                    if (ensureDataLoaded()) {
                        scheduler.generateTimetable();
                        waitForEnter();
                    }
                    break;
                case "3":
                    if (ensureTimetableGenerated()) {
                        System.out.print("Enter year group (1-4): ");
                        String yr = scanner.nextLine().trim();
                        try {
                            int y = Integer.parseInt(yr);
                            if (y >= 1 && y <= 4) {
                                timetable.printYearGroupTimetable(y, dataLoader.getInstructors());
                            } else {
                                System.out.println("Invalid year group (1-4).");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid numeric input.");
                        }
                        waitForEnter();
                    }
                    break;
                case "4":
                    if (ensureTimetableGenerated()) {
                        timetable.printAllScheduledSessions(dataLoader.getInstructors());
                        waitForEnter();
                    }
                    break;
                case "5":
                    if (ensureTimetableGenerated()) {
                        timetable.printUnscheduledAndWarnings();
                        waitForEnter();
                    }
                    break;
                case "6":
                    System.out.println("\n[Checkpoint 2 - Code v2] Manual modification will be completed in Week 11 (Final Release).");
                    waitForEnter();
                    break;
                case "7":
                    System.out.println("\n[Checkpoint 2 - Code v2] Classroom request email generation will be completed in Week 12 (Final Release).");
                    waitForEnter();
                    break;
                case "8":
                    if (ensureTimetableGenerated()) {
                        try {
                            timetable.saveToFile(DEFAULT_OUTPUT_DIR, dataLoader.getInstructors());
                            System.out.println("Timetable saved to " + DEFAULT_OUTPUT_DIR + "/timetable.txt");
                        } catch (IOException e) {
                            System.out.println("Error saving file: " + e.getMessage());
                        }
                        waitForEnter();
                    }
                    break;
                case "0":
                    System.out.println("Exiting program. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter 0-8.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n==================================================");
        System.out.println("  DEPARTMENT COURSE TIMETABLE MANAGEMENT SYSTEM");
        System.out.println("       Software Engineering Department (v2)");
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

    private boolean ensureDataLoaded() {
        if (!dataLoader.isDataLoaded()) {
            dataLoader.loadAllData(DEFAULT_DATA_DIR);
        }
        return dataLoader.isDataLoaded();
    }

    private boolean ensureTimetableGenerated() {
        if (timetable.getAllSessions().isEmpty()) {
            System.out.println("Please generate the timetable first (Option 2).");
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
}

