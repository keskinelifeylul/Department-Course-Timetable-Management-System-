package v1;

/**
 * Course.java (Code v1 - Project Skeleton)
 * Represents a university course offered to one of the four undergraduate year groups.
 */
public class Course {
    private String courseCode;
    private String title;
    private int yearGroup;
    private int theoryHours;
    private int labHours;
    private int studentCount;
    private String assignedInstructorId;
    private String requiredRoomType;

    public Course(String courseCode, String title, int yearGroup, int theoryHours,
                  int labHours, int studentCount, String assignedInstructorId, String requiredRoomType) {
        this.courseCode = courseCode;
        this.title = title;
        this.yearGroup = yearGroup;
        this.theoryHours = theoryHours;
        this.labHours = labHours;
        this.studentCount = studentCount;
        this.assignedInstructorId = assignedInstructorId;
        this.requiredRoomType = requiredRoomType;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYearGroup() {
        return yearGroup;
    }

    public void setYearGroup(int yearGroup) {
        this.yearGroup = yearGroup;
    }

    public int getTheoryHours() {
        return theoryHours;
    }

    public void setTheoryHours(int theoryHours) {
        this.theoryHours = theoryHours;
    }

    public int getLabHours() {
        return labHours;
    }

    public void setLabHours(int labHours) {
        this.labHours = labHours;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public String getAssignedInstructorId() {
        return assignedInstructorId;
    }

    public void setAssignedInstructorId(String assignedInstructorId) {
        this.assignedInstructorId = assignedInstructorId;
    }

    public String getRequiredRoomType() {
        return requiredRoomType;
    }

    public void setRequiredRoomType(String requiredRoomType) {
        this.requiredRoomType = requiredRoomType;
    }

    @Override
    public String toString() {
        return courseCode + " - " + title + " (Year " + yearGroup + ", Students: " + studentCount + ")";
    }
}


