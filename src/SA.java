import java.util.*;
import java.io.*;
import java.text.*;

/**
* Performs the Simulated Annealing(SA) on the KTH data set.
*/
public class SA implements Algorithm {

    private KTH kth;
    private double START_TEMPERATURE;
    private double FINAL_TEMPERATURE = 0.7;
    private double HEATING;
    private int runLaps = 0;
    private int bestSol = Integer.MIN_VALUE;
    private int DESIRED_FITNESS = 0;
    private double temp;

    public SA(boolean isHybrid) {
        if (isHybrid){
            START_TEMPERATURE = 80;
            HEATING = 0.9998;
        } else {
            START_TEMPERATURE = 100;
            HEATING = 0.9995;
        }
        kth = new KTH();
    }

    /**
     * Returns a schedule based on the given constraints
     */
    public TimeTable generateTimeTable() {
        // create the initial random population
        TimeTable currentSolution = createTrivialSolution();
        return runSA(currentSolution);
    }

    public TimeTable generateTimeTable(TimeTable currentSolution){
        return runSA(currentSolution);
    }

    private TimeTable runSA(TimeTable currentSolution){
        TimeTable bestSolution = new TimeTable(currentSolution);
        bestSolution.getFitness(kth);
        DecimalFormat numberFormat = new DecimalFormat("#.00");

        temp = START_TEMPERATURE;
        runLaps = 0;
        long startTime = System.currentTimeMillis();
        while (temp > FINAL_TEMPERATURE && bestSol < DESIRED_FITNESS) {
            runLaps++;
            TimeTable newSolution = changeSomething(currentSolution);

            // Få de olika värdena för lösningarna
            int currentVal = currentSolution.getFitness(kth);
            int newVal = newSolution.getFitness(kth);
            double accept = accept(currentVal, newVal, temp);
            //System.out.println("Temp: " + numberFormat.format(temp) + ",\tNuvarande: " + currentVal + ",\tNy: " + newVal + ",\tAccept: " + accept);
            // Slumpa om vi ska acceptera lösningen eller inte
            if (accept > Math.random()) {
                //System.out.println("Accepterade lösning.");
                currentSolution = new TimeTable(newSolution);
            } else {
                //System.out.println("Accepterade inte lösning.");
            }

            // Kom ihåg om det är hittills bästa lösningen
            if (currentSolution.getFitness(true) > bestSolution.getFitness(true)) {
                bestSolution = new TimeTable(currentSolution);
                bestSol = currentSolution.getFitness(true);
            }
            System.out.println(numberFormat.format((System.currentTimeMillis()-startTime)/1000.0) + ";" + runLaps + ";" + bestSolution.getFitness(true) + ";" + numberFormat.format(temp));

            // Make it cold and fuzzy
            temp *= HEATING;
        }

        return bestSolution;
    }

    /**
     * Räknar ut ett accepterat värde för lösningen.
     */
    public static double accept(int value, int newValue, double temperature) {
        // Om lösningen är bättre, acceptera direkt
        if (newValue >= value) {
            return 1.0;
        }
        double delta = newValue - value;
        // Om nya lösningen är sämre, acceptera kanske (räkna ut sannolikhetsvärde)
        double val = Math.exp(50 * delta / temperature);
        return val;
    }

    public TimeTable changeSomething(TimeTable currentSolution) {
        // Skapa ny lösning utifrån den gamla (en enkel kopia)
        TimeTable newSolution = new TimeTable(currentSolution);
        int temp1 = 0; 
        int temp2 = 0;
        int rtt1timeslot = 0, rtt2timeslot = 0, rtt1day = 0, rtt2day = 0, rand1 = 0, rand2 = 0;
        RoomTimeTable rtt1 = null, rtt2 = null;
        RoomTimeTable[] rtts = newSolution.getRoomTimeTables();
        int eventsPerRoom = RoomTimeTable.NUM_TIMESLOTS * RoomTimeTable.NUM_DAYS;
        int interval = kth.getNumRooms() * eventsPerRoom;
        int tests = 0;
        Random random = new Random();
        while ((temp1 == 0 && temp2 == 0) || temp1 == temp2) {
            rand1 = random.nextInt(interval);
            rand2 = random.nextInt(interval);

            rtt1 = rtts[rand1 / eventsPerRoom];
            rtt2 = rtts[rand2 / eventsPerRoom];
            rtt1day = (rand1 % eventsPerRoom) / RoomTimeTable.NUM_TIMESLOTS; 
            rtt2day = (rand2 % eventsPerRoom) / RoomTimeTable.NUM_TIMESLOTS; 
            rtt1timeslot = (rand1 % eventsPerRoom) % RoomTimeTable.NUM_TIMESLOTS;
            rtt2timeslot = (rand2 % eventsPerRoom) % RoomTimeTable.NUM_TIMESLOTS;
            temp1 = rtt1.getEvent(rtt1day, rtt1timeslot);
            temp2 = rtt2.getEvent(rtt2day, rtt2timeslot);
            tests++;
        }
        rtt1.setEvent(rtt1day, rtt1timeslot, temp2);
        rtt2.setEvent(rtt2day, rtt2timeslot, temp1);
        newSolution.putRoomTimeTable(rand1 / eventsPerRoom, rtt1);
        newSolution.putRoomTimeTable(rand2 / eventsPerRoom, rtt2);
        
        //System.out.println("Event 1: " + temp1 + ". Event 2: " + temp2 + ".");

        // TODO Check not to break capacity limits
        return newSolution;
    }

    public TimeTable createTrivialSolution() {
        Map<Integer, Room> rooms = kth.getRooms();
        int numRooms = kth.getRooms().size();

        ArrayList<TimeSlot> availableTimeSlots = new ArrayList<TimeSlot>();
        for (int roomId : rooms.keySet()) {
            for (int d = 0; d < RoomTimeTable.NUM_DAYS; d++) {
                for (int t = 0; t < RoomTimeTable.NUM_TIMESLOTS; t++) {
                    availableTimeSlots.add(new TimeSlot(roomId, d, t));
                }
            }
        }

        TimeTable tt = new TimeTable(numRooms);
        for (int roomId : rooms.keySet()) {
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
        for (Event e : kth.getEvents().values()) {
            TimeSlot availableTimeSlot = availableTimeSlots.get(rand.nextInt(availableTimeSlots.size()));
            RoomTimeTable rtt = tt.getRoomTimeTables()[availableTimeSlot.roomId];
            rtt.setEvent(availableTimeSlot.day, availableTimeSlot.timeSlot, e.getId());
            availableTimeSlots.remove(availableTimeSlot);
        }
        availableTimeSlots.clear();

        return tt;
    }

    public void printConf() {
        System.out.println("Algorithm:           \tSimulated annealing");
        System.out.println("Number of iterations:\t" + runLaps);
        System.out.println("Best solution:       \t" + bestSol);
        System.out.println("Desired fitness:     \t" + DESIRED_FITNESS);
        System.out.println("Final temperature:   \t" + temp);
    }


    public void loadData(String dataFileUrl) {
        kth.clear(); // reset all previous data before loading

        try {
            File file = new File(dataFileUrl);
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = null;
            // input data sections are read in the following order separated by #
            // #rooms <name> <capacity> <type>
            // #courses <id> <name> <numLectures> <numClasses> <numLabs>
            // #lecturers <name> <course>+
            // #studentgroups <name> <numStudents> <course>+
            String readingSection = null;
            String roomName = null;
            String courseName = null;
            String lecturerName = null;
            String studentGroupName = null;
            HashMap<String, Integer> courseNameToId = new HashMap<String, Integer>();
            while((line = in.readLine()) != null) {
                String[] data = line.split(" ");
                if(data[0].charAt(0) == '#') {
                    readingSection = data[1];
                    data = in.readLine().split(" ");
                }
                if(readingSection.equals("ROOMS")) {
                  roomName = data[0];
                  int cap = Integer.parseInt(data[1]);
                  Event.Type type = Event.generateType(Integer.parseInt(data[2]));
                  Room room = new Room(roomName, cap, type);
                  kth.addRoom(room);
              } else if(readingSection.equals("COURSES")) {
                  courseName = data[0];
                  int numLectures = Integer.parseInt(data[1]);
                  int numLessons = Integer.parseInt(data[2]);
                  int numLabs = Integer.parseInt(data[3]);
                  Course course = new Course(courseName, numLectures, numLessons, numLabs);
                  courseNameToId.put(courseName, course.getId());
                  kth.addCourse(course);
              } else if(readingSection.equals("LECTURERS")) {
                  lecturerName = data[0];
                  Lecturer lecturer = new Lecturer(lecturerName);
                  for(int i = 1; i < data.length; i++) {
            // register all courses that this lecturer may teach
                    courseName = data[i];
                    lecturer.addCourse(kth.getCourses().get(courseNameToId.get(courseName)));
                }
                kth.addLecturer(lecturer);
            } else if(readingSection.equals("STUDENTGROUPS")) {
              studentGroupName = data[0];
              int size = Integer.parseInt(data[1]);
              StudentGroup studentGroup = new StudentGroup(studentGroupName, size);
              for(int i = 2; i < data.length; i++) {
                courseName = data[i];
                studentGroup.addCourse(kth.getCourses().get(courseNameToId.get(courseName)));
            }
            kth.addStudentGroup(studentGroup);
        }
    }
      kth.createEvents(); // create all events
      in.close();
  } catch (FileNotFoundException e) {
      e.printStackTrace();
  } catch (IOException e) {
      e.printStackTrace();
  }
}
}
