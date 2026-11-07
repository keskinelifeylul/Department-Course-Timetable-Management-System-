package v3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles loading and validating external data from CSV and TXT files.
 */
public class DataLoader {
    private Map<String, Course> courses = new LinkedHashMap<>();
    private Map<String, Instructor> instructors = new LinkedHashMap<>();
    private Map<String, Room> rooms = new LinkedHashMap<>();
    private List<SpecialRequest> specialRequests = new ArrayList<>();
    private List<String> loadWarnings = new ArrayList<>();
    private boolean dataLoaded = false;

    public Map<String, Course> getCourses() {
        return courses;
    }

    public Map<String, Instructor> getInstructors() {
        return instructors;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public List<SpecialRequest> getSpecialRequests() {
        return specialRequests;
    }

    public List<String> getLoadWarnings() {
        return loadWarnings;
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    public void loadAllData(String dataDir) {
        courses.clear();
        instructors.clear();
        rooms.clear();
        specialRequests.clear();
        loadWarnings.clear();

        System.out.println("\n=== LOAD INPUT DATA ===");

        int courseCount = loadCoursesFile(dataDir + "/courses.csv");
        System.out.printf("Reading %s/courses.csv ........ %d courses loaded%n", dataDir, courseCount);

        int instructorCount = loadInstructorsFile(dataDir + "/instructors.txt");
        System.out.printf("Reading %s/instructors.txt .... %d instructors loaded%n", dataDir, instructorCount);

        int roomCount = loadRoomsFile(dataDir + "/rooms.csv");
        System.out.printf("Reading %s/rooms.csv .......... %d rooms loaded%n", dataDir, roomCount);

        int[] reqCounts = loadConstraintsFile(dataDir + "/constraints.txt");
        System.out.printf("Reading %s/constraints.txt .... %d requests loaded (%d mandatory, %d preferred)%n",
                dataDir, reqCounts[0] + reqCounts[1], reqCounts[0], reqCounts[1]);

        List<String> invalidCourseKeys = new ArrayList<>();
        for (Course c : courses.values()) {
            Instructor inst = instructors.get(c.getInstructorId());
            if (inst == null) {
                loadWarnings.add(String.format("courses.csv: instructor \"%s\" for course \"%s\" not found -> course skipped",
                        c.getInstructorId(), c.getCourseCode()));
                invalidCourseKeys.add(c.getCourseCode());
            } else {
                inst.addAssignedCourse(c.getCourseCode());
            }
        }
        for (String key : invalidCourseKeys) {
            courses.remove(key);
        }

        if (!loadWarnings.isEmpty()) {
            System.out.println("\nWARNINGS:");
            for (String w : loadWarnings) {
                System.out.println("  " + w);
            }
            System.out.printf("%nData loaded with %d warning(s).%n", loadWarnings.size());
        } else {
            System.out.println("\nData loaded successfully with 0 warnings.");
        }

        dataLoaded = !courses.isEmpty() && !rooms.isEmpty();
    }

    private int loadCoursesFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            loadWarnings.add(file.getName() + ": file not found -> no courses loaded");
            return 0;
        }
        int count = 0;
        int lineNum = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNum++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String[] parts = trimmed.split(",");
                if (parts.length < 7) {
                    loadWarnings.add(String.format("%s, line %d: incomplete record -> record skipped", file.getName(), lineNum));
                    continue;
                }
                String code = parts[0].trim();
                String title = parts[1].trim();
                int yearGroup;
                int theoryHours;
                int labHours;
                int students;

                try {
                    yearGroup = Integer.parseInt(parts[2].trim());
                    if (yearGroup < 1 || yearGroup > 4) {
                        loadWarnings.add(String.format("%s, line %d: invalid year group \"%s\" -> record skipped",
                                file.getName(), lineNum, parts[2].trim()));
                        continue;
                    }
                } catch (NumberFormatException e) {
                    loadWarnings.add(String.format("%s, line %d: invalid value \"%s\" for year group -> record skipped",
                            file.getName(), lineNum, parts[2].trim()));
                    continue;
                }

                try {
                    theoryHours = Integer.parseInt(parts[3].trim());
                } catch (NumberFormatException e) {
                    loadWarnings.add(String.format("%s, line %d: invalid value \"%s\" for theory hours -> record skipped",
                            file.getName(), lineNum, parts[3].trim()));
                    continue;
                }

                try {
                    labHours = Integer.parseInt(parts[4].trim());
                } catch (NumberFormatException e) {
                    loadWarnings.add(String.format("%s, line %d: invalid value \"%s\" for lab hours -> record skipped",
                            file.getName(), lineNum, parts[4].trim()));
                    continue;
                }

                try {
                    students = Integer.parseInt(parts[5].trim());
                } catch (NumberFormatException e) {
                    loadWarnings.add(String.format("%s, line %d: invalid value \"%s\" for student count -> record skipped",
                            file.getName(), lineNum, parts[5].trim()));
                    continue;
                }

                String instructorId = parts[6].trim();
                String reqRoomType = parts.length >= 8 ? parts[7].trim() : "Classroom";

                Course course = new Course(code, title, yearGroup, theoryHours, labHours, students, instructorId, reqRoomType);
                courses.put(code, course);
                count++;
            }
        } catch (IOException e) {
            loadWarnings.add(file.getName() + ": error reading file (" + e.getMessage() + ")");
        }
        return count;
    }

    private int loadInstructorsFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            loadWarnings.add(file.getName() + ": file not found -> no instructors loaded");
            return 0;
        }
        int count = 0;
        int lineNum = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNum++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String[] parts = trimmed.split("\\|");
                if (parts.length < 2) {
                    loadWarnings.add(String.format("%s, line %d: incomplete instructor record -> record skipped",
                            file.getName(), lineNum));
                    continue;
                }
                String id = parts[0].trim();
                String name = parts[1].trim();
                String unavailSpec = parts.length >= 3 ? parts[2].trim() : "NONE";
                String prefSpec = parts.length >= 4 ? parts[3].trim() : "NONE";

                Instructor inst = new Instructor(id, name, prefSpec);
                if (!unavailSpec.equalsIgnoreCase("NONE") && !unavailSpec.isEmpty()) {
                    String[] slotSpecs = unavailSpec.split(";");
                    for (String spec : slotSpecs) {
                        TimeSlot ts = TimeSlot.parse(spec);
                        if (ts != null) {
                            inst.addUnavailableSlot(ts);
                        } else {
                            loadWarnings.add(String.format("%s, line %d: invalid unavailable slot \"%s\" -> ignored",
                                    file.getName(), lineNum, spec));
                        }
                    }
                }
                instructors.put(id, inst);
                count++;
            }
        } catch (IOException e) {
            loadWarnings.add(file.getName() + ": error reading file (" + e.getMessage() + ")");
        }
        return count;
    }

    private int loadRoomsFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            loadWarnings.add(file.getName() + ": file not found -> no rooms loaded");
            return 0;
        }
        int count = 0;
        int lineNum = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNum++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String[] parts = trimmed.split(",");
                if (parts.length < 3) {
                    loadWarnings.add(String.format("%s, line %d: incomplete room record -> record skipped",
                            file.getName(), lineNum));
                    continue;
                }
                String roomId = parts[0].trim();
                String roomType = parts[1].trim();
                int capacity;
                try {
                    capacity = Integer.parseInt(parts[2].trim());
                } catch (NumberFormatException e) {
                    loadWarnings.add(String.format("%s, line %d: invalid capacity \"%s\" -> record skipped",
                            file.getName(), lineNum, parts[2].trim()));
                    continue;
                }

                Room room = new Room(roomId, roomType, capacity);
                if (parts.length >= 4) {
                    String unavailSpec = parts[3].trim();
                    if (!unavailSpec.equalsIgnoreCase("NONE") && !unavailSpec.isEmpty()) {
                        String[] slotSpecs = unavailSpec.split(";");
                        for (String spec : slotSpecs) {
                            TimeSlot ts = TimeSlot.parse(spec);
                            if (ts != null) {
                                room.addUnavailableSlot(ts);
                            }
                        }
                    }
                }
                rooms.put(roomId, room);
                count++;
            }
        } catch (IOException e) {
            loadWarnings.add(file.getName() + ": error reading file (" + e.getMessage() + ")");
        }
        return count;
    }

    private int[] loadConstraintsFile(String filePath) {
        int mandatoryCount = 0;
        int preferredCount = 0;
        File file = new File(filePath);
        if (!file.exists()) {
            loadWarnings.add(file.getName() + ": file not found -> no special requests loaded");
            return new int[]{0, 0};
        }
        int lineNum = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNum++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String[] parts = trimmed.split("\\|");
                if (parts.length < 4) {
                    loadWarnings.add(String.format("%s, line %d: incomplete constraint record -> request ignored",
                            file.getName(), lineNum));
                    continue;
                }
                String courseCode = parts[0].trim();
                String priorityStr = parts[1].trim().toUpperCase();
                String typeStr = parts[2].trim().toUpperCase();
                String valueStr = parts[3].trim();

                if (!courses.containsKey(courseCode)) {
                    loadWarnings.add(String.format("%s, line %d: course \"%s\" not found -> request ignored",
                            file.getName(), lineNum, courseCode));
                    continue;
                }

                if ("REQUIRED_ROOM".equals(typeStr) && !rooms.containsKey(valueStr)) {
                    loadWarnings.add(String.format("%s, line %d: room \"%s\" not found -> request ignored",
                            file.getName(), lineNum, valueStr));
                    continue;
                }

                boolean isMandatory = "MANDATORY".equals(priorityStr);
                SpecialRequest req = new SpecialRequest(courseCode, isMandatory, typeStr, valueStr);
                specialRequests.add(req);
                if (isMandatory) {
                    mandatoryCount++;
                } else {
                    preferredCount++;
                }
            }
        } catch (IOException e) {
            loadWarnings.add(file.getName() + ": error reading file (" + e.getMessage() + ")");
        }
        return new int[]{mandatoryCount, preferredCount};
    }
}

