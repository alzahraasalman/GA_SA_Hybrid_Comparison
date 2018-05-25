public class TimeSlot {
    public int roomId;
    public int day;
    public int timeSlot;
    public boolean available = true;
    public TimeSlot(int roomId, int day, int timeSlot) {
        this.roomId = roomId;
        this.day = day;
        this.timeSlot = timeSlot;
    }
}