import java.util.*;

public class TimeTable implements Comparable<TimeTable> {
    protected int fitness;

    // The timetables for each room
    protected RoomTimeTable[] roomTimeTables;

    public TimeTable(TimeTable tt) {
        fitness = tt.fitness;
        roomTimeTables = new RoomTimeTable[tt.roomTimeTables.length];
        for (int i = 0; i < tt.roomTimeTables.length; i++) {
            roomTimeTables[i] = new RoomTimeTable(tt.roomTimeTables[i]);
        }
    }

    public TimeTable(int numRooms) {
        roomTimeTables = new RoomTimeTable[numRooms];
    }

    public int getFitness(KTH kth) {
        fitness(kth);
        return getFitness(true);
    }

    public int getFitness() {
        return getFitness(true);
    }

    public int getFitness(boolean var) {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public RoomTimeTable[] getRoomTimeTables() {
        return roomTimeTables;
    }

    public void putRoomTimeTable(int i, RoomTimeTable rtt) {
        roomTimeTables[i] = rtt;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (RoomTimeTable rtt : roomTimeTables) {
            sb.append(rtt.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    // sorts descending
    @Override
    public int compareTo(TimeTable other) {
        int otherFitness = other.getFitness();

        if (fitness > otherFitness)
            return -1;
        else if (fitness == otherFitness)
            return 0;
        else
            return 1;
    }

    //////////////////////////
  // FITNESS
  //////////////////////////

    public void fitness(KTH kth) {
    // set the fitness to this time table
        int studentGroupDoubleBookings = studentGroupDoubleBookings(kth);
        int lecturerDoubleBookings = lecturerDoubleBookings(kth);
        int roomCapacityBreaches = roomCapacityBreaches(kth);
        int roomTypeBreaches = roomTypeBreaches(kth);

        int numBreaches = studentGroupDoubleBookings * 2+
            lecturerDoubleBookings +
            roomCapacityBreaches * 4 +
            roomTypeBreaches * 4;

        int fitness = -1 * numBreaches;
        setFitness(fitness);
    }


  //////////////////////////
  // CONSTRAINTS
  //////////////////////////

  ///////////////////
  // Hard constraints, each function returns the number of constraint breaches
  ///////////////////

  // NOTE: Two of the hard constraints are solved by the chosen datastructure
  // Invalid timeslots may not be used
  // A room can not be double booked at a certain timeslot

    private int studentGroupDoubleBookings(KTH kth) {
        int numBreaches = 0;

        RoomTimeTable[] rtts = roomTimeTables;

        for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS;
            timeslot++) {
            for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
                for (StudentGroup sg : kth.getStudentGroups().values()) {

                    HashMap<Integer, Integer> eventGroupCounts = 
                    new HashMap<Integer, Integer>();

                    for (RoomTimeTable rtt : rtts) {
                        int eventID = rtt.getEvent(day, timeslot);

                        // only look at booked timeslots
                        if (eventID != 0) {
                            Event event = kth.getEvent(eventID);
                            int sgID = event.getStudentGroup().getId();

                            // if this bookings is for the current studentgroup
                            if (sgID == sg.getId()) {
                                int eventGroupID = event.getEventGroupId();

                                // increment the count for this event group id
                                if (!eventGroupCounts.containsKey(eventGroupID)) {
                                    eventGroupCounts.put(eventGroupID, 1);

                                } else {
                                    int oldCount = eventGroupCounts.get(eventGroupID);
                                    eventGroupCounts.put(eventGroupID, oldCount + 1);
                                }
                            }
                        }
                    }

                    // find the biggest event group
                    int biggestGroup; 
                    int biggestGroupSize = 0;
                    int sumGroupSize = 0;
                    for (Map.Entry<Integer, Integer> entry : eventGroupCounts.entrySet()) {
                        sumGroupSize += entry.getValue();

                        if (entry.getValue() > biggestGroupSize) {
                            biggestGroup = entry.getKey();
                            biggestGroupSize = entry.getValue();
                        }
                    }

                    numBreaches += sumGroupSize - biggestGroupSize;
                }
            }
        }

        return numBreaches;
    }

    private int max(int a, int b, int c) {
        int max = a;

        if (b > max) {
            max = b;
        }

        if (c > max) {
            max = c;
        }

        return max;
    }

  // num times a lecturer is double booked
  // NOTE: lecturers are only booked to lectures
  // for the labs and classes, TAs are used and they are assumed to always
  // be available
    private int lecturerDoubleBookings(KTH kth) {
        int numBreaches = 0;

        RoomTimeTable[] rtts = roomTimeTables;

        for (Lecturer lecturer : kth.getLecturers().values()) {

      // for each time
            for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS;
             timeslot++) {

                for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
                    int numBookings = 0;

                    for (RoomTimeTable rtt : rtts) {
                        int eventID = rtt.getEvent(day, timeslot);

            // 0 is unbooked
                        if (eventID != 0) {
                            Event event = kth.getEvent(eventID);
              // only check lectures since lecturers are only
              // attached to lecture events
                            if (event.getType() == Event.Type.LECTURE) {
                                if (event.getLecturer().getId() == lecturer.getId()) {
                                    numBookings++;
                                }
                            }
                        }
                    }

          // only one booking per time is allowed
                    if (numBookings > 1) {

            // add all extra bookings to the number of constraint breaches
                        numBreaches += numBookings - 1;
                    }
                }
            }
        }

        return numBreaches;
    }

  // num times a room is too small for the event booked
    private int roomCapacityBreaches(KTH kth) {
        int numBreaches = 0;

        RoomTimeTable[] rtts = roomTimeTables;

        for (RoomTimeTable rtt : rtts) {
            int roomSize = rtt.getRoom().getCapacity();

      // for each time
            for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS;
              timeslot++) {

                for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
                    int eventID = rtt.getEvent(day, timeslot);

          // only look at booked timeslots
                    if (eventID != 0) {
                        int eventSize = kth.getEvent(eventID).getSize();
                        if (roomSize < eventSize) {
                            numBreaches++;
                        }
                    }
                }
            }
        }

        return numBreaches;
    }

  // num times an event is booked to the wrong room type
    private int roomTypeBreaches(KTH kth) {
        int numBreaches = 0;

        RoomTimeTable[] rtts = roomTimeTables;

        for (RoomTimeTable rtt : rtts) {
            Event.Type roomType = rtt.getRoom().getType();

      // for each time
            for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS;
              timeslot++) {

                for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
                    int eventID = rtt.getEvent(day, timeslot);

          // only look at booked timeslots
                    if (eventID != 0) {
                        Event.Type type = kth.getEvent(eventID).getType();
                        if (roomType != type) {
                            numBreaches++;
                        }
                    }
                }
            }
        }

        return numBreaches;
    }
}
