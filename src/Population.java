import java.util.*;

// keeps all the TimeTables for a generation
public class Population {

  // time slot class used when creating the random population
  public class TimeSlot {
    private int roomId;
    private int day;
    private int timeSlot;
    private boolean available = true;
    public TimeSlot(int roomId, int day, int timeSlot) {
      this.roomId = roomId;
      this.day = day;
      this.timeSlot = timeSlot;
    }
  }

  // should be ordered when selecting the best individuals
  private LinkedList<TimeTable> individuals;

  public Population() {
    individuals = new LinkedList<TimeTable>();
  }

  public void createRandomIndividuals(int numIndividuals, KTH kth) {
    Map<Integer, Room> rooms = kth.getRooms();
    int numRooms = kth.getRooms().size();

    for(int i = 0; i < numIndividuals; i++) {
      // register all available timeslots
      ArrayList<TimeSlot> availableTimeSlots = new ArrayList<TimeSlot>();
      for(int roomId : rooms.keySet()) {
        for(int d = 0; d < RoomTimeTable.NUM_DAYS; d++) {
          for(int t = 0; t < RoomTimeTable.NUM_TIMESLOTS; t++) {
            availableTimeSlots.add(new TimeSlot(roomId, d, t));
          }
        }
      }

      TimeTable tt = new TimeTable(numRooms);
      for(int roomId : rooms.keySet()) {
        Room room = rooms.get(roomId);
        RoomTimeTable rtt = new RoomTimeTable(room);
        tt.putRoomTimeTable(roomId, rtt);
      }

      // index variables
      int rttId = 0;
      int day = 0;
      int timeSlot = 0;

      // assign all event to any randomly selected available timeslot
      Random rand = new Random(System.currentTimeMillis());
      for(Event e : kth.getEvents().values()) {
        TimeSlot availableTimeSlot = availableTimeSlots.get(rand.nextInt(availableTimeSlots.size()));
        RoomTimeTable rtt = tt.getRoomTimeTables()[availableTimeSlot.roomId];
        rtt.setEvent(availableTimeSlot.day, availableTimeSlot.timeSlot, e.getId());
        availableTimeSlots.remove(availableTimeSlot);
        /* DEBUG
        System.out.println("==============");
        System.out.println("ROOM TIME TABLE ID: " + rtt.getRoom().getName());
        System.out.println("Day: " + availableTimeSlot.day + " Timeslot: " + availableTimeSlot.timeSlot + " Event ID: " + e.getId());
        */
      }
      individuals.add(tt);
      availableTimeSlots.clear();
    }
  }

  // assumes sorted
  public TimeTable getTopIndividual() {
    return individuals.get(0);
  }

  public TimeTable getWorstIndividual() {
    return individuals.getLast();
  }

  public void addIndividual(TimeTable tt) {
    individuals.add(tt);
  }

  public TimeTable getIndividual(int i) {
    return individuals.get(i);
  }

  public void addIndividualSorted(TimeTable tt) {
    ListIterator<TimeTable> it = individuals.listIterator();
    ListIterator<TimeTable> it2 = individuals.listIterator();

    while (it.hasNext()) {
      if (it.next().getFitness() < tt.getFitness()) {
        it2.add(tt);
        break;
      }

      it2.next();
    }
  }

  public ListIterator<TimeTable> listIterator() {
    return individuals.listIterator();
  }

  public void sortIndividuals() {
    Collections.sort(individuals);
  }

  public int size() {
    return individuals.size();
  }

}
