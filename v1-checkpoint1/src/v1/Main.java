package v1;

import java.util.Scanner;

/**
 * Main.java (Code v1 - Project Skeleton for Week 6 / Checkpoint 1)
 * Provides the console main menu (Screen 1) and demonstrates class structure consistency.
 */
public class Main {
    public static void main(String[] args) {
        Timetable timetable = new Timetable();
        Scheduler scheduler = new Scheduler(timetable);
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            printMainMenu();
            System.out.print("Select an option: ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    scheduler.loadInputData();
                    break;
                case "2":
                    scheduler.generateTimetableAutomatically();
                    break;
                case "3":
                    System.out.print("Enter year group (1-4): ");
                    if (scanner.hasNextLine()) {
                        String yr = scanner.nextLine().trim();
                        try {
                            timetable.displayTimetableByYear(Integer.parseInt(yr));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid year group.");
                        }
                    }
                    break;
                case "4":
                    timetable.displayAllScheduledSessions();
                    break;
                case "5":
                    timetable.displayUnscheduledAndWarnings();
                    break;
                case "6":
                    scheduler.modifyOrRemoveAssignment("SENG101-T", null, null);
                    break;
                case "7":
                    scheduler.generateClassroomRequestEmail();
                    break;
                case "8":
                    timetable.saveToFile("output/timetable.txt");
                    break;
                case "0":
                    System.out.println("Exiting program. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please select between 0 and 8.");
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
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
}


