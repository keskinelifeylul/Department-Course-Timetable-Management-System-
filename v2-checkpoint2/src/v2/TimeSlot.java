package v2;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a continuous time block on a specific working day.
 * Working days: Mon, Tue, Wed, Thu, Fri
 * Working hours: 09:00 to 17:00 (one-hour slots).
 */
public class TimeSlot {
    public static final List<String> WORKING_DAYS = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri");
    public static final int DAY_START_HOUR = 9;
    public static final int DAY_END_HOUR = 17;

    private String day;
    private int startHour;
    private int endHour;

    public TimeSlot(String day, int startHour, int endHour) {
        this.day = normalizeDay(day);
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public static String normalizeDay(String rawDay) {
        if (rawDay == null) {
            return "";
        }
        String trimmed = rawDay.trim();
        if (trimmed.length() >= 3) {
            String prefix = trimmed.substring(0, 3).toLowerCase();
            switch (prefix) {
                case "mon": return "Mon";
                case "tue": return "Tue";
                case "wed": return "Wed";
                case "thu": return "Thu";
                case "fri": return "Fri";
                default: return trimmed;
            }
        }
        return trimmed;
    }

    public static String getFullDayName(String shortDay) {
        switch (normalizeDay(shortDay)) {
            case "Mon": return "Monday";
            case "Tue": return "Tuesday";
            case "Wed": return "Wednesday";
            case "Thu": return "Thursday";
            case "Fri": return "Friday";
            default: return shortDay;
        }
    }

    public static int getDayIndex(String day) {
        return WORKING_DAYS.indexOf(normalizeDay(day));
    }

    public String getDay() {
        return day;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getDuration() {
        return endHour - startHour;
    }

    public boolean isWithinWorkingHours() {
        return WORKING_DAYS.contains(day)
                && startHour >= DAY_START_HOUR
                && endHour <= DAY_END_HOUR
                && startHour < endHour;
    }

    public boolean overlapsWith(TimeSlot other) {
        if (other == null || !this.day.equalsIgnoreCase(other.day)) {
            return false;
        }
        return this.startHour < other.endHour && other.startHour < this.endHour;
    }

    public boolean isAfter(TimeSlot other) {
        if (other == null) {
            return true;
        }
        int thisDayIdx = getDayIndex(this.day);
        int otherDayIdx = getDayIndex(other.day);
        if (thisDayIdx != otherDayIdx) {
            return thisDayIdx > otherDayIdx;
        }
        return this.startHour >= other.endHour;
    }

    public boolean isMorning() {
        return endHour <= 12;
    }

    public boolean isAfternoon() {
        return startHour >= 13;
    }

    public String formatHours() {
        return String.format("%02d:00-%02d:00", startHour, endHour);
    }

    @Override
    public String toString() {
        return day + " " + formatHours();
    }

    public static TimeSlot parse(String spec) {
        if (spec == null || spec.trim().isEmpty()) {
            return null;
        }
        String[] parts = spec.trim().split(":");
        if (parts.length < 2) {
            return null;
        }
        String dayPart = normalizeDay(parts[0]);
        String timePart = spec.trim().substring(parts[0].length() + 1);
        String[] range = timePart.split("-");
        if (range.length != 2) {
            return null;
        }
        int start = parseHour(range[0]);
        int end = parseHour(range[1]);
        if (start < 0 || end < 0 || start >= end) {
            return null;
        }
        return new TimeSlot(dayPart, start, end);
    }

    public static int parseHour(String timeStr) {
        try {
            String cleaned = timeStr.trim();
            if (cleaned.contains(":")) {
                cleaned = cleaned.split(":")[0];
            }
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

