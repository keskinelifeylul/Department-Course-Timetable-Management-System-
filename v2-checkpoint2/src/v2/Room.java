package v2;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a department Classroom or Laboratory with capacity and availability restrictions.
 */
public class Room {
    private String roomId;
    private String roomType;
    private int capacity;
    private List<TimeSlot> unavailableSlots;

    public Room(String roomId, String roomType, int capacity) {
        this.roomId = roomId.trim();
        this.roomType = roomType.trim();
        this.capacity = capacity;
        this.unavailableSlots = new ArrayList<>();
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<TimeSlot> getUnavailableSlots() {
        return unavailableSlots;
    }

    public void addUnavailableSlot(TimeSlot slot) {
        if (slot != null) {
            unavailableSlots.add(slot);
        }
    }

    public boolean hasSufficientCapacity(int studentCount) {
        return this.capacity >= studentCount;
    }

    public boolean matchesType(String requiredType) {
        return this.roomType.equalsIgnoreCase(requiredType);
    }

    public boolean isAvailable(TimeSlot candidate) {
        for (TimeSlot unavail : unavailableSlots) {
            if (unavail.overlapsWith(candidate)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, Capacity: %d)", roomId, roomType, capacity);
    }
}

