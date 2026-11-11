public class TimeSlot {
    public static final String[] DAY_NAMES = {"Mon", "Tue", "Wed", "Thu", "Fri"};
    public static final int FIRST_HOUR = 9;
    public static final int LAST_HOUR = 17;

    private int day;
    private int startHour;
    private int length;

    public TimeSlot(int day, int startHour, int length) {
        this.day = day;
        this.startHour = startHour;
        this.length = length;
    }

    public int getDay() {
        return day;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getLength() {
        return length;
    }

    public int getEndHour() {
        return startHour + length;
    }

    public boolean overlaps(TimeSlot other) {
        if (other == null) {
            return false;
        }
        return this.day == other.day
                && this.startHour < other.getEndHour()
                && other.startHour < this.getEndHour();
    }

    public boolean isWithinWorkingHours() {
        return day >= 0 && day < DAY_NAMES.length
                && length > 0
                && startHour >= FIRST_HOUR
                && getEndHour() <= LAST_HOUR;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TimeSlot)) {
            return false;
        }
        TimeSlot other = (TimeSlot) obj;
        return this.day == other.day
                && this.startHour == other.startHour
                && this.length == other.length;
    }

    @Override
    public String toString() {
        String dayName = (day >= 0 && day < DAY_NAMES.length) ? DAY_NAMES[day] : "Day" + day;
        return String.format("%s %02d:00-%02d:00", dayName, startHour, getEndHour());
    }
}
