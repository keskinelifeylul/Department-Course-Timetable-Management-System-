package v1;

/**
 * TimeSlot.java (Code v1 - Project Skeleton)
 * Represents a weekly scheduling block (Day, Start Hour, End Hour).
 */
public class TimeSlot {
    private String day;
    private int startHour;
    private int endHour;

    public TimeSlot(String day, int startHour, int endHour) {
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getDuration() {
        return endHour - startHour;
    }

    public boolean isWithinWorkingHours() {
        return startHour >= 9 && endHour <= 17 && startHour < endHour;
    }

    public boolean overlapsWith(TimeSlot other) {
        if (other == null || !this.day.equalsIgnoreCase(other.day)) {
            return false;
        }
        return this.startHour < other.endHour && other.startHour < this.endHour;
    }

    @Override
    public String toString() {
        return String.format("%s %02d:00-%02d:00", day, startHour, endHour);
    }
}


