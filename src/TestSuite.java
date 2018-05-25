import java.io.*;
import java.util.*;
import java.lang.*;

public class TestSuite {
    Scanner scanner;

    long start, end;

    // genetic algorithm
    Algorithm algorithm;

    TimeTable bestTimeTable;

    public TestSuite() {
        System.out.println("[***] Which algorithm do you want to run?");
        System.out.println("Your alternatives:");
        System.out.println("0. Genetic algorithm");
        System.out.println("1. Simulated annealing");
        System.out.println("2. Hybrid algorithm");
        System.out.print("> ");
        scanner = new Scanner(System.in);
        int ans = scanner.nextInt();
        scanner.nextLine(); // Consume to next line break
        System.out.println();

        if (ans == 0) {
            setupGA(false);
        } else if (ans == 1){
            setupSA(false);
        } else {
            setupHY();
        }

        // Display countdown
        countdown(0);

        start = System.currentTimeMillis();
        System.out.println();

        System.out.println(":::::::::::::::::::::::::::::::");
        System.out.println(":::::::::: Start run ::::::::::");
        System.out.println(":::::::::::::::::::::::::::::::");
        System.out.println("Start time: " + start);
        run();
        end = System.currentTimeMillis();

        System.out.println(":::::::::::::::::::::::::::::::");
        System.out.println("::::: Generated timetable :::::");
        System.out.println(":::::::::::::::::::::::::::::::");
        printTimeTable(bestTimeTable);
        System.out.println();
        System.out.println(":::::::::::::::::::::::::::::::");
        System.out.println("::::::::::::: Info ::::::::::::");
        System.out.println(":::::::::::::::::::::::::::::::");
        algorithm.printConf();
        System.out.println("Start timestamp:\t" + start);
        System.out.println("End timestamp:  \t" + end);
        System.out.println("Total time:     \t" + (end - start) / 1000.0 + "s");
    }

    private void countdown(int seconds) {
        System.out.print("Starting in " + seconds);
        for (int i = seconds - 1; i >= 0; i--) {
            for (int j = 0; j < 5; j++) {
                sleep(200);
                System.out.print(".");
            }
            System.out.print(i);
        }
    }

    /**
     * Splits sleep up into 50 small parts.
     */
    private void sleep(int milliseconds) {
        long now = System.currentTimeMillis();
        long stop = now + milliseconds;
        while (stop > now) {
            try {
                Thread.sleep(milliseconds / 50);
            } catch (InterruptedException e) {}
            now = System.currentTimeMillis();
        }
    }

    private void setupSA(boolean isHybrid) {
        System.out.println("Simulated annealing selected.");
        SA sa = new SA(isHybrid);
        sa.loadData(getString("path to data set", "../input/kth_L"));
        algorithm = sa;
    }

    private void setupGA(boolean isHybrid) {
        System.out.println("Genetic algorithm selected.");
        GA ga = new GA(isHybrid);
        ga.loadData(getString("path to data set", "../input/kth_L"));
        ga.setMutationProbability(getInt("mutation probability", 30));
        ga.setCrossoverProbability(getInt("crossover probability", 30));
        ga.setPopulationSize(getInt("population size", 30));
        ga.setSelectionSize(getInt("selection size", 50));
        ga.setSelectionType(getInt("selection type", 1));
        ga.setMutationType(getInt("mutation type", 0));
        algorithm = ga;
    }

    private void setupHY(){
        System.out.println("Hybrid algorithm selected.");
        setupGA(true);
        GA ga = (GA) algorithm;
        setupSA(true);
        SA sa = (SA) algorithm;
        Hybrid hybrid = new Hybrid(ga, sa);
        algorithm = hybrid;
    }

    private int getInt(String name, int def) {
        // Ask user
        System.out.println("[***] Specify " + name + ". (Press enter to use " + def + ")");
        System.out.print("> ");
        int data;
        try {
            data = Integer.parseInt(scanner.nextLine().trim());
            System.out.println("Using " + name + " " + data + ".");
        } catch (NumberFormatException e) {
            data = def;
            System.out.println("Using standard " + name + " (" + data+ ").");
        }
        System.out.println();
        return data;
    }

    private String getString(String name, String def) {
        // Ask user
        System.out.println("[***] Specify " + name + ". (Press enter to use '" + def + "')");
        System.out.print("> ");
        String data1 = scanner.nextLine().trim();
        if (data1.length() == 0) {
            System.out.println("Using standard data set ('" + def + "').");
            data1 = def;
        } else {
            System.out.println("Using dataset '" + data1 + "'.");
        }
        System.out.println();
        return data1;
    }

    private void run() {
        bestTimeTable = algorithm.generateTimeTable();
    }

    public void printTimeTable(TimeTable tt) {
        StringBuilder sb = new StringBuilder();
        int nrSlots = 0;
        int nrEvents = 0;
        for(RoomTimeTable rtt : tt.getRoomTimeTables()) {
            sb.append("============ ");    
            sb.append("Room: " + rtt.getRoom().getName() + " Capacity: " + rtt.getRoom().getCapacity());
            sb.append(" ============\n");   
            for (int timeslot = 0; timeslot < RoomTimeTable.NUM_TIMESLOTS; timeslot++) {
                for (int day = 0; day < RoomTimeTable.NUM_DAYS; day++) {
                    int eventId = rtt.getEvent(day, timeslot);
                    if(eventId > nrEvents) {
                        nrEvents = eventId; 
                    }
                    nrSlots++;
                    sb.append("[\t" + eventId + "\t]");
                }
                sb.append("\n");
            }    
        }
        System.out.println(sb.toString());
        System.out.println("Fitness:         \t" + tt.getFitness());
        System.out.println("Number of slots: \t" + nrSlots);
        System.out.println("Number of events:\t" + nrEvents);
        System.out.println("Sparseness:      \t" + ((double)nrEvents/(double)nrSlots));
    }
}
