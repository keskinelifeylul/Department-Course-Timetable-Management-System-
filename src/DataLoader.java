import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

public class DataLoader {
    private HashMap<String, Instructor> instructors;
    private HashMap<String, Course> courses;
    private HashMap<String, Room> rooms;
    private ArrayList<SpecialRequest> requests;
    private ArrayList<String> warnings;

    public DataLoader() {
        this.instructors = new LinkedHashMap<>();
        this.courses = new LinkedHashMap<>();
        this.rooms = new LinkedHashMap<>();
        this.requests = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public HashMap<String, Instructor> getInstructors() {
        return instructors;
    }

    public HashMap<String, Course> getCourses() {
        return courses;
    }

    public HashMap<String, Room> getRooms() {
        return rooms;
    }

    public ArrayList<SpecialRequest> getRequests() {
        return requests;
    }

    public ArrayList<String> getWarnings() {
        return warnings;
    }

    public boolean loadAll(String dataDir) {
        instructors.clear();
        courses.clear();
        rooms.clear();
        requests.clear();
        warnings.clear();

        String base = (dataDir == null || dataDir.trim().isEmpty()) ? "data" : dataDir.trim();
        String instructorsPath = base + "/instructors.txt";
        String coursesPath = base + "/courses.csv";
        String roomsPath = base + "/rooms.csv";
        String requestsPath = base + "/constraints.txt";

        loadInstructors(instructorsPath);
        loadCourses(coursesPath);
        loadRooms(roomsPath);
        loadRequests(requestsPath);

        boolean coursesExists = new File(coursesPath).isFile();
        boolean roomsExists = new File(roomsPath).isFile();
        return coursesExists && roomsExists && !courses.isEmpty() && !rooms.isEmpty();
    }

    private void loadInstructors(String path) {
        String fileName = new File(path).getName();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (lineNum == 1 && line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length != 4) {
                    warnings.add(fileName + ", line " + lineNum + ": expected 4 fields, found "
                            + parts.length + " -> record skipped");
                    continue;
                }
                String id = parts[0].trim();
                String name = parts[1].trim();
                String unavailText = parts[2].trim();
                String preferred = parts[3].trim().toUpperCase(Locale.ROOT);

                if (id.isEmpty()) {
                    warnings.add(fileName + ", line " + lineNum
                            + ": empty instructor ID -> record skipped");
                    continue;
                }
                if (instructors.containsKey(id)) {
                    warnings.add(fileName + ", line " + lineNum + ": duplicate instructor ID \""
                            + id + "\" -> record skipped");
                    continue;
                }
                if (name.isEmpty()) {
                    warnings.add(fileName + ", line " + lineNum
                            + ": empty instructor name -> record skipped");
                    continue;
                }
                if (!preferred.equals("MORNING") && !preferred.equals("AFTERNOON")
                        && !preferred.equals("NONE")) {
                    warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                            + parts[3].trim() + "\" for preferred time -> record skipped");
                    continue;
                }
                ArrayList<TimeSlot> unavailSlots;
                try {
                    unavailSlots = parseSlots(unavailText);
                } catch (IllegalArgumentException e) {
                    warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                            + unavailText + "\" for unavailable periods -> record skipped");
                    continue;
                }

                Instructor instructor = new Instructor(id, name, preferred);
                for (TimeSlot slot : unavailSlots) {
                    instructor.addUnavailableSlot(slot);
                }
                instructors.put(id, instructor);
            }
        } catch (FileNotFoundException e) {
            warnings.add(path + " not found");
        } catch (IOException e) {
            warnings.add("Error reading " + path + ": " + e.getMessage());
        }
    }

    private void loadCourses(String path) {
        String fileName = new File(path).getName();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            int lineNum = 0;
            boolean headerSkipped = false;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (lineNum == 1 && line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length != 7) {
                    warnings.add(fileName + ", line " + lineNum + ": expected 7 fields, found "
                            + parts.length + " -> record skipped");
                    continue;
                }
                String code = parts[0].trim();
                String title = parts[1].trim();
                String yearText = parts[2].trim();
                String instructorId = parts[3].trim();
                String theoryText = parts[4].trim();
                String labText = parts[5].trim();
                String studentsText = parts[6].trim();

                if (code.isEmpty()) {
                    warnings.add(fileName + ", line " + lineNum
                            + ": empty course code -> record skipped");
                    continue;
                }
                if (courses.containsKey(code)) {
                    warnings.add(fileName + ", line " + lineNum + ": duplicate course code \""
                            + code + "\" -> record skipped");
                    continue;
                }
                if (title.isEmpty()) {
                    warnings.add(fileName + ", line " + lineNum
                            + ": empty course title -> record skipped");
                    continue;
                }
                int year;
                try {
                    year = Integer.parseInt(yearText);
                    if (year < 1 || year > 4) {
                        warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                                + yearText + "\" for year (expected 1-4) -> record skipped");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                            + yearText + "\" for year -> record skipped");
                    continue;
                }

                Instructor instructor = instructors.get(instructorId);
                if (instructor == null) {
                    warnings.add(fileName + ", line " + lineNum + ": instructor \""
                            + instructorId + "\" not found -> record skipped");
                    continue;
                }

                int theoryHours;
                try {
                    theoryHours = Integer.parseInt(theoryText);
                    if (theoryHours < 0) {
                        warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                                + theoryText + "\" for theory hours -> record skipped");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                            + theoryText + "\" for theory hours -> record skipped");
                    continue;
                }

                int labHours;
                try {
                    labHours = Integer.parseInt(labText);
                    if (labHours < 0) {
                        warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                                + labText + "\" for lab hours -> record skipped");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                            + labText + "\" for lab hours -> record skipped");
                    continue;
                }

                if (theoryHours == 0 && labHours == 0) {
                    warnings.add(fileName + ", line " + lineNum
                            + ": both theory hours and lab hours are 0 -> record skipped");
                    continue;
                }

                int students;
                try {
                    students = Integer.parseInt(studentsText);
                    if (students <= 0) {
                        warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                                + studentsText + "\" for students -> record skipped");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                            + studentsText + "\" for students -> record skipped");
                    continue;
                }

                Course course = new Course(code, title, year, instructor, theoryHours, labHours, students);
                course.createSessions();
                instructor.addCourse(course);
                courses.put(code, course);
            }
        } catch (FileNotFoundException e) {
            warnings.add(path + " not found");
        } catch (IOException e) {
            warnings.add("Error reading " + path + ": " + e.getMessage());
        }
    }

    private void loadRooms(String path) {
        String fileName = new File(path).getName();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            int lineNum = 0;
            boolean headerSkipped = false;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (lineNum == 1 && line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    warnings.add(fileName + ", line " + lineNum + ": expected 4 fields, found "
                            + parts.length + " -> record skipped");
                    continue;
                }
                String roomId = parts[0].trim();
                String typeText = parts[1].trim().toUpperCase(Locale.ROOT);
                String capText = parts[2].trim();
                String unavailText = parts[3].trim();

                if (roomId.isEmpty()) {
                    warnings.add(fileName + ", line " + lineNum
                            + ": empty room ID -> record skipped");
                    continue;
                }
                if (rooms.containsKey(roomId)) {
                    warnings.add(fileName + ", line " + lineNum + ": duplicate room ID \""
                            + roomId + "\" -> record skipped");
                    continue;
                }

                RoomType type;
                try {
                    type = RoomType.valueOf(typeText);
                } catch (IllegalArgumentException e) {
                    warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                            + parts[1].trim() + "\" for room type -> record skipped");
                    continue;
                }

                int capacity;
                try {
                    capacity = Integer.parseInt(capText);
                    if (capacity <= 0) {
                        warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                                + capText + "\" for capacity -> record skipped");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                            + capText + "\" for capacity -> record skipped");
                    continue;
                }

                ArrayList<TimeSlot> unavailSlots;
                try {
                    unavailSlots = parseSlots(unavailText);
                } catch (IllegalArgumentException e) {
                    warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                            + unavailText + "\" for unavailable periods -> record skipped");
                    continue;
                }

                Room room = new Room(roomId, type, capacity);
                for (TimeSlot slot : unavailSlots) {
                    room.addUnavailableSlot(slot);
                }
                rooms.put(roomId, room);
            }
        } catch (FileNotFoundException e) {
            warnings.add(path + " not found");
        } catch (IOException e) {
            warnings.add("Error reading " + path + ": " + e.getMessage());
        }
    }

    private void loadRequests(String path) {
        String fileName = new File(path).getName();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (lineNum == 1 && line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                if (parts.length != 5) {
                    warnings.add(fileName + ", line " + lineNum + ": expected 5 fields, found "
                            + parts.length + " -> request ignored");
                    continue;
                }
                String reqTypeText = parts[0].trim().toUpperCase(Locale.ROOT);
                String courseCode = parts[1].trim();
                String sessionText = parts[2].trim().toUpperCase(Locale.ROOT);
                String value = parts[3].trim();
                String priorityText = parts[4].trim().toUpperCase(Locale.ROOT);

                RequestType reqType;
                try {
                    reqType = RequestType.valueOf(reqTypeText);
                } catch (IllegalArgumentException e) {
                    warnings.add(fileName + ", line " + lineNum + ": invalid request type \""
                            + parts[0].trim() + "\" -> request ignored");
                    continue;
                }

                Course course = courses.get(courseCode);
                if (course == null) {
                    warnings.add(fileName + ", line " + lineNum + ": course \""
                            + courseCode + "\" not found -> request ignored");
                    continue;
                }

                SessionType sessionType;
                if (sessionText.equals("T")) {
                    sessionType = SessionType.THEORY;
                } else if (sessionText.equals("L")) {
                    sessionType = SessionType.LAB;
                } else if (sessionText.equals("-")) {
                    sessionType = null;
                } else {
                    warnings.add(fileName + ", line " + lineNum + ": invalid session field \""
                            + parts[2].trim() + "\" -> request ignored");
                    continue;
                }

                boolean mandatory;
                if (priorityText.equals("MANDATORY")) {
                    mandatory = true;
                } else if (priorityText.equals("PREFERRED")) {
                    mandatory = false;
                } else {
                    warnings.add(fileName + ", line " + lineNum + ": invalid priority \""
                            + parts[4].trim() + "\" -> request ignored");
                    continue;
                }

                TimeSlot slot = null;
                String roomId = null;

                if (reqType == RequestType.FIXED_TIME) {
                    if (sessionType == null) {
                        warnings.add(fileName + ", line " + lineNum
                                + ": FIXED_TIME request requires session T or L -> request ignored");
                        continue;
                    }
                    int expectedHours = (sessionType == SessionType.THEORY)
                            ? course.getTheoryHours()
                            : course.getLabHours();
                    if (expectedHours <= 0) {
                        warnings.add(fileName + ", line " + lineNum + ": course \"" + courseCode
                                + "\" has no " + sessionType + " session -> request ignored");
                        continue;
                    }
                    String[] tokens = value.split("\\s+");
                    if (tokens.length != 2) {
                        warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                                + value + "\" for FIXED_TIME -> request ignored");
                        continue;
                    }
                    try {
                        ArrayList<TimeSlot> parsed = parseSlots(value);
                        if (parsed.size() != 1) {
                            warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                                    + value + "\" for FIXED_TIME -> request ignored");
                            continue;
                        }
                        slot = parsed.get(0);
                    } catch (IllegalArgumentException e) {
                        warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                                + value + "\" for FIXED_TIME -> request ignored");
                        continue;
                    }
                    if (slot.getLength() != expectedHours) {
                        warnings.add(fileName + ", line " + lineNum + ": FIXED_TIME length ("
                                + slot.getLength() + " h) does not match session hours ("
                                + expectedHours + " h) -> request ignored");
                        continue;
                    }
                } else if (reqType == RequestType.SPECIFIC_ROOM) {
                    if (sessionType != null) {
                        int expectedHours = (sessionType == SessionType.THEORY)
                                ? course.getTheoryHours()
                                : course.getLabHours();
                        if (expectedHours <= 0) {
                            warnings.add(fileName + ", line " + lineNum + ": course \"" + courseCode
                                    + "\" has no " + sessionType + " session -> request ignored");
                            continue;
                        }
                    }
                    if (!rooms.containsKey(value)) {
                        warnings.add(fileName + ", line " + lineNum + ": room \""
                                + value + "\" not found -> request ignored");
                        continue;
                    }
                    roomId = value;
                } else if (reqType == RequestType.UNAVAILABLE_DAY) {
                    int dayIdx = parseDay(value);
                    if (dayIdx < 0) {
                        warnings.add(fileName + ", line " + lineNum + ": invalid day \""
                                + value + "\" for UNAVAILABLE_DAY -> request ignored");
                        continue;
                    }
                    slot = new TimeSlot(dayIdx, TimeSlot.FIRST_HOUR,
                            TimeSlot.LAST_HOUR - TimeSlot.FIRST_HOUR);
                    if (mandatory) {
                        course.getInstructor().addUnavailableSlot(slot);
                    }
                } else if (reqType == RequestType.LAB_BEFORE_THEORY) {
                    if (!value.equals("-") && !value.isEmpty()) {
                        warnings.add(fileName + ", line " + lineNum + ": invalid value \""
                                + value + "\" for LAB_BEFORE_THEORY -> request ignored");
                        continue;
                    }
                }

                requests.add(new SpecialRequest(reqType, courseCode, sessionType, slot, roomId, mandatory));
            }
        } catch (FileNotFoundException e) {
            warnings.add(path + " not found");
        } catch (IOException e) {
            warnings.add("Error reading " + path + ": " + e.getMessage());
        }
    }

    private ArrayList<TimeSlot> parseSlots(String text) {
        ArrayList<TimeSlot> result = new ArrayList<>();
        if (text == null) {
            return result;
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("NONE")) {
            return result;
        }
        String[] entries = trimmed.split(";", -1);
        for (String entry : entries) {
            String item = entry.trim();
            if (item.isEmpty()) {
                throw new IllegalArgumentException("Empty slot entry");
            }
            String[] tokens = item.split("\\s+");
            if (tokens.length == 1) {
                int day = parseDay(tokens[0]);
                if (day < 0) {
                    throw new IllegalArgumentException("Invalid day: " + tokens[0]);
                }
                result.add(new TimeSlot(day, TimeSlot.FIRST_HOUR,
                        TimeSlot.LAST_HOUR - TimeSlot.FIRST_HOUR));
            } else if (tokens.length == 2) {
                int day = parseDay(tokens[0]);
                if (day < 0) {
                    throw new IllegalArgumentException("Invalid day: " + tokens[0]);
                }
                String[] range = tokens[1].split("-", -1);
                if (range.length != 2) {
                    throw new IllegalArgumentException("Invalid time range: " + tokens[1]);
                }
                int start = parseHour(range[0]);
                int end = parseHour(range[1]);
                if (start < TimeSlot.FIRST_HOUR || end > TimeSlot.LAST_HOUR || start >= end) {
                    throw new IllegalArgumentException("Hours out of working range: " + tokens[1]);
                }
                result.add(new TimeSlot(day, start, end - start));
            } else {
                throw new IllegalArgumentException("Invalid slot format: " + item);
            }
        }
        return result;
    }

    private int parseDay(String token) {
        if (token == null) {
            return -1;
        }
        String t = token.trim();
        for (int i = 0; i < TimeSlot.DAY_NAMES.length; i++) {
            if (TimeSlot.DAY_NAMES[i].equalsIgnoreCase(t)) {
                return i;
            }
        }
        return -1;
    }

    private int parseHour(String token) {
        String t = token.trim();
        if (t.endsWith(":00")) {
            t = t.substring(0, t.length() - 3);
        }
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hour: " + token);
        }
    }
}
