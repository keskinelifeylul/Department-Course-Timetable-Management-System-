package v3;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a department course offered to one of the four undergraduate year groups.
 */
public class Course {
    private String courseCode;
    private String title;
    private int yearGroup;
    private int theoryHours;
    private int labHours;
    private int studentCount;
    private String instructorId;
    private String requiredRoomType;

    public Course(String courseCode, String title, int yearGroup, int theoryHours,
                  int labHours, int studentCount, String instructorId, String requiredRoomType) {
        this.courseCode = courseCode.trim();
        this.title = title.trim();
        this.yearGroup = yearGroup;
        this.theoryHours = theoryHours;
        this.labHours = labHours;
        this.studentCount = studentCount;
        this.instructorId = instructorId.trim();
        this.requiredRoomType = (requiredRoomType == null || requiredRoomType.trim().isEmpty())
                ? "Classroom" : requiredRoomType.trim();
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTitle() {
        return title;
    }

    public int getYearGroup() {
        return yearGroup;
    }

    public int getTheoryHours() {
        return theoryHours;
    }

    public int getLabHours() {
        return labHours;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public String getRequiredRoomType() {
        return requiredRoomType;
    }

    public List<Session> createSessions() {
        List<Session> sessions = new ArrayList<>();
        if (theoryHours > 0) {
            sessions.add(new Session(courseCode + "-T", this, false, theoryHours, "Classroom"));
        }
        if (labHours > 0) {
            sessions.add(new Session(courseCode + "-L", this, true, labHours, "Laboratory"));
        }
        return sessions;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (Year %d, Theory: %dh, Lab: %dh, Students: %d, Instructor: %s)",
                courseCode, title, yearGroup, theoryHours, labHours, studentCount, instructorId);
    }
}

