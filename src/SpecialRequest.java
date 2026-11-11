public class SpecialRequest {
    private RequestType type;
    private String courseCode;
    private SessionType sessionType;
    private TimeSlot slot;
    private String roomId;
    private boolean mandatory;

    public SpecialRequest(RequestType type, String courseCode, SessionType sessionType,
                          TimeSlot slot, String roomId, boolean mandatory) {
        this.type = type;
        this.courseCode = courseCode;
        this.sessionType = sessionType;
        this.slot = slot;
        this.roomId = roomId;
        this.mandatory = mandatory;
    }

    public RequestType getType() {
        return type;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public TimeSlot getSlot() {
        return slot;
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean appliesTo(Session session) {
        if (session == null) {
            return false;
        }
        if (!session.getCourse().getCode().equalsIgnoreCase(courseCode)) {
            return false;
        }
        return sessionType == null || sessionType == session.getType();
    }

    public boolean isMandatory() {
        return mandatory;
    }
}
