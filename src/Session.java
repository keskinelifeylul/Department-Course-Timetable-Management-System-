public class Session {
    private Course course;
    private SessionType type;
    private int hours;
    private TimeSlot slot;
    private Room room;
    private ProblemType problemType;
    private String problemReason;

    public Session(Course course, SessionType type, int hours) {
        this.course = course;
        this.type = type;
        this.hours = hours;
        this.slot = null;
        this.room = null;
        this.problemType = null;
        this.problemReason = null;
    }

    public Course getCourse() {
        return course;
    }

    public SessionType getType() {
        return type;
    }

    public int getHours() {
        return hours;
    }

    public TimeSlot getSlot() {
        return slot;
    }

    public Room getRoom() {
        return room;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public String getProblemReason() {
        return problemReason;
    }

    public String getName() {
        return course.getCode() + (type == SessionType.THEORY ? "-T" : "-L");
    }

    public RoomType getRequiredRoomType() {
        return type == SessionType.THEORY ? RoomType.CLASSROOM : RoomType.LABORATORY;
    }

    public void assign(TimeSlot slot, Room room) {
        this.slot = slot;
        this.room = room;
        this.problemType = null;
        this.problemReason = null;
    }

    public void unassign() {
        this.slot = null;
        this.room = null;
        this.problemType = null;
        this.problemReason = null;
    }

    public void markUnscheduled(ProblemType p, String reason) {
        this.slot = null;
        this.room = null;
        this.problemType = p;
        this.problemReason = reason;
    }

    public boolean isScheduled() {
        return slot != null && room != null;
    }

    @Override
    public String toString() {
        if (isScheduled()) {
            return getName() + " " + slot + " " + room.getRoomId();
        }
        return getName() + " [UNSCHEDULED]";
    }
}
