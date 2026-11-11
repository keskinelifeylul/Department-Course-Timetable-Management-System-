import java.io.File;
import java.util.Scanner;

public class ConsoleMenu {
    private Scanner scanner;
    private String dataDir;
    private DataLoader loader;
    private Timetable timetable;
    private Scheduler scheduler;
    private ReportGenerator reports;
    private boolean dataLoaded;

    public ConsoleMenu(String dataDir) {
        this.scanner = new Scanner(System.in);
        this.dataDir = (dataDir == null || dataDir.trim().isEmpty()) ? "data" : dataDir.trim();
        this.loader = new DataLoader();
        this.timetable = new Timetable();
        this.scheduler = null;
        this.reports = new ReportGenerator(timetable);
        this.dataLoaded = false;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Select an option: ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice \"" + input + "\". Please enter a number from 0 to 8.");
                continue;
            }

            switch (choice) {
                case 1:
                    loadData();
                    break;
                case 2:
                    generateTimetable();
                    break;
                case 3:
                    viewYearTimetable();
                    break;
                case 4:
                    viewAllSessions();
                    break;
                case 5:
                    viewUnscheduled();
                    break;
                case 6:
                    modifyAssignment();
                    break;
                case 7:
                    classroomRequest();
                    break;
                case 8:
                    saveToFile();
                    break;
                case 0:
                    System.out.println("Goodbye.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice \"" + input + "\". Please enter a number from 0 to 8.");
                    break;
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("==================================================");
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

    private void loadData() {
        System.out.println();
        System.out.println("=== LOAD INPUT DATA ===");
        dataLoaded = loader.loadAll(dataDir);

        timetable = new Timetable();
        for (Course course : loader.getCourses().values()) {
            for (Session session : course.createSessions()) {
                timetable.addSession(session);
            }
        }
        reports = new ReportGenerator(timetable);
        scheduler = null;

        printFileStatus(dataDir + "/courses.csv", loader.getCourses().size() + " courses loaded");
        printFileStatus(dataDir + "/instructors.txt", loader.getInstructors().size() + " instructors loaded");
        printFileStatus(dataDir + "/rooms.csv", loader.getRooms().size() + " rooms loaded");

        int mandatoryCount = 0;
        int preferredCount = 0;
        for (SpecialRequest req : loader.getRequests()) {
            if (req.isMandatory()) {
                mandatoryCount++;
            } else {
                preferredCount++;
            }
        }
        printFileStatus(dataDir + "/constraints.txt",
                loader.getRequests().size() + " requests loaded ("
                        + mandatoryCount + " mandatory, " + preferredCount + " preferred)");

        if (!loader.getWarnings().isEmpty()) {
            System.out.println();
            System.out.println("WARNINGS:");
            for (String w : loader.getWarnings()) {
                System.out.println("  " + w);
            }
        }

        System.out.println();
        if (dataLoaded) {
            System.out.println("Data loaded with " + loader.getWarnings().size() + " warning(s). "
                    + timetable.getSessions().size() + " session(s) created.");
        } else {
            System.out.println("Data loading completed with missing/empty required files ("
                    + loader.getWarnings().size() + " warning(s)).");
        }
        System.out.print("Press Enter to return to the main menu...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    private void printFileStatus(String filePath, String summary) {
        if (new File(filePath).isFile()) {
            System.out.println("Reading " + filePath + " .... " + summary);
        } else {
            System.out.println("Reading " + filePath + " .... NOT FOUND");
        }
    }

    private void generateTimetable() {
        System.out.println("Generate timetable automatically: not implemented yet (planned for V2).");
    }

    private void viewYearTimetable() {
        System.out.println("View timetable by year group: not implemented yet (planned for V2).");
    }

    private void viewAllSessions() {
        System.out.println("View all scheduled sessions: not implemented yet (planned for V2).");
    }

    private void viewUnscheduled() {
        System.out.println("View unscheduled sessions and warnings: not implemented yet (planned for V2).");
    }

    private void modifyAssignment() {
        System.out.println("Modify / remove an assignment: not implemented yet (planned for V3).");
    }

    private void classroomRequest() {
        System.out.println("Generate classroom request email: not implemented yet (planned for V3).");
    }

    private void saveToFile() {
        System.out.println("Save timetable to file: not implemented yet (planned for V3).");
    }
}
