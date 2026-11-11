import java.util.ArrayList;

public class Room {
    private String roomId;
    private RoomType type;
    private int capacity;
    private ArrayList<TimeSlot> unavailableSlots;

    public Room(String roomId, RoomType type, int capacity) {
        this.roomId = roomId;
        this.type = type;
        this.capacity = capacity;
        this.unavailableSlots = new ArrayList<>();
    }

    public String getRoomId() {
        return roomId;
    }

    public RoomType getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public ArrayList<TimeSlot> getUnavailableSlots() {
        return unavailableSlots;
    }

    public boolean isSuitableFor(Session session) {
        // TODO: planned for V2 (Code v3)
        return false;
    }

    public boolean isAvailable(TimeSlot slot) {
        if (slot == null) {
            return false;
        }
        for (TimeSlot unavailable : unavailableSlots) {
            if (unavailable.overlaps(slot)) {
                return false;
            }
        }
        return true;
    }

    public void addUnavailableSlot(TimeSlot slot) {
        if (slot != null) {
            unavailableSlots.add(slot);
        }
    }

    @Override
    public String toString() {
        return roomId + " (" + type + ", cap " + capacity + ")";
    }
}
