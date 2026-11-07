package v1;

import java.util.ArrayList;
import java.util.List;

/**
 * Room.java (Code v1 - Project Skeleton)
 * Represents a classroom or laboratory with capacity and availability information.
 */
public class Room {
    private String roomId;
    private String roomType;
    private int capacity;
    private List<TimeSlot> unavailableSlots;

    public Room(String roomId, String roomType, int capacity) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.capacity = capacity;
        this.unavailableSlots = new ArrayList<>();
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<TimeSlot> getUnavailableSlots() {
        return unavailableSlots;
    }

    public void addUnavailableSlot(TimeSlot slot) {
        unavailableSlots.add(slot);
    }

    public boolean hasSufficientCapacity(int studentCount) {
        return this.capacity >= studentCount;
    }

    public boolean matchesType(String requiredType) {
        return this.roomType != null && this.roomType.equalsIgnoreCase(requiredType);
    }

    public boolean isAvailable(TimeSlot slot) {
        for (TimeSlot unavailable : unavailableSlots) {
            if (unavailable.overlapsWith(slot)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return roomId + " (" + roomType + ", Capacity: " + capacity + ")";
    }
}


