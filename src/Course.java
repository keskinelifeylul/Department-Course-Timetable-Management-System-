import java.util.ArrayList;

public class Course {
    private String code;
    private String title;
    private int year;
    private Instructor instructor;
    private int theoryHours;
    private int labHours;
    private int students;
    private ArrayList<Session> sessions;

    public Course(String code, String title, int year, Instructor instructor,
                  int theoryHours, int labHours, int students) {
        this.code = code;
        this.title = title;
        this.year = year;
        this.instructor = instructor;
        this.theoryHours = theoryHours;
        this.labHours = labHours;
        this.students = students;
        this.sessions = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public int getTheoryHours() {
        return theoryHours;
    }

    public int getLabHours() {
        return labHours;
    }

    public int getStudents() {
        return students;
    }

    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public ArrayList<Session> createSessions() {
        if (sessions.isEmpty()) {
            if (theoryHours > 0) {
                sessions.add(new Session(this, SessionType.THEORY, theoryHours));
            }
            if (labHours > 0) {
                sessions.add(new Session(this, SessionType.LAB, labHours));
            }
        }
        return sessions;
    }

    public int getTotalHours() {
        return theoryHours + labHours;
    }

    @Override
    public String toString() {
        return code + " - " + title;
    }
}
