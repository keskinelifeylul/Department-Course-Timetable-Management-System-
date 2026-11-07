package v2;

/**
 * Represents a single schedulable teaching block (either Theory -T or Laboratory -L) of a Course.
 */
public class Session {
    private String sessionId;
    private Course course;
    private boolean isLab;
    private int durationHours;
    private String requiredRoomType;

    private TimeSlot assignedTimeSlot;
    private Room assignedRoom;
    private boolean scheduled;
    private String problemType;
    private String unscheduledReason;
    private int rejectedAttempts;
    private String placementNote;

    public Session(String sessionId, Course course, boolean isLab, int durationHours, String requiredRoomType) {
        this.sessionId = sessionId;
        this.course = course;
        this.isLab = isLab;
        this.durationHours = durationHours;
        this.requiredRoomType = requiredRoomType;
        this.scheduled = false;
        this.rejectedAttempts = 0;
        this.placementNote = "";
    }

    public String getSessionId() {
        return sessionId;
    }

    public Course getCourse() {
        return course;
    }

    public boolean isLab() {
        return isLab;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public String getRequiredRoomType() {
        return requiredRoomType;
    }

    public int getStudentCount() {
        return course.getStudentCount();
    }

    public int getYearGroup() {
        return course.getYearGroup();
    }

    public String getInstructorId() {
        return course.getInstructorId();
    }

    public TimeSlot getAssignedTimeSlot() {
        return assignedTimeSlot;
    }

    public Room getAssignedRoom() {
        return assignedRoom;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public String getProblemType() {
        return problemType;
    }

    public String getUnscheduledReason() {
        return unscheduledReason;
    }

    public int getRejectedAttempts() {
        return rejectedAttempts;
    }

    public void setRejectedAttempts(int rejectedAttempts) {
        this.rejectedAttempts = rejectedAttempts;
    }

    public String getPlacementNote() {
        return placementNote;
    }

    public void setPlacementNote(String placementNote) {
        this.placementNote = placementNote;
    }

    public void assign(TimeSlot timeSlot, Room room) {
        this.assignedTimeSlot = timeSlot;
        this.assignedRoom = room;
        this.scheduled = true;
        this.problemType = null;
        this.unscheduledReason = null;
    }

    public void unassign(String problemType, String reason) {
        this.assignedTimeSlot = null;
        this.assignedRoom = null;
        this.scheduled = false;
        this.problemType = problemType;
        this.unscheduledReason = reason;
    }

    @Override
    public String toString() {
        if (scheduled) {
            return String.format("%-10s %-30s Year %d  %s  %-6s",
                    sessionId, course.getTitle(), getYearGroup(), assignedTimeSlot, assignedRoom.getRoomId());
        } else {
            return String.format("%-10s %-30s Year %d  [UNSCHEDULED: %s]",
                    sessionId, course.getTitle(), getYearGroup(), unscheduledReason);
        }
    }
}

