/**
 * Created by alzahraasalman on 2018-03-17.
 */
public class Hybrid implements Algorithm{

    private GA ga;
    private SA sa;

    public Hybrid(GA ga, SA sa){
        this.ga = ga;
        this.sa = sa;
    }

    public TimeTable generateTimeTable(){
        TimeTable bestGa = ga.generateTimeTable();
        return sa.generateTimeTable(bestGa);
    }

    public void printConf(){
        ga.printConf();
        sa.printConf();
    }
}
