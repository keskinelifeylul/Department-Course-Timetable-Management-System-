package v2;

/**
 * Represents a special scheduling request or constraint from constraints.txt.
 */
public class SpecialRequest {
    private String courseCode;
    private boolean mandatory;
    private String constraintType;
    private String value;

    public SpecialRequest(String courseCode, boolean mandatory, String constraintType, String value) {
        this.courseCode = courseCode.trim();
        this.mandatory = mandatory;
        this.constraintType = constraintType.trim().toUpperCase();
        this.value = value.trim();
    }

    public String getCourseCode() {
        return courseCode;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public String getConstraintType() {
        return constraintType;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %s: %s",
                courseCode, mandatory ? "MANDATORY" : "PREFERRED", constraintType, value);
    }
}

