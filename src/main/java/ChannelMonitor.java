import java.util.ArrayList;
import java.util.Random;

public class ChannelMonitor implements Runnable {

    private ArrayList<AirportHangar> airportHangars;
    private Random rand;

    public ChannelMonitor(ArrayList<AirportHangar> airportHangars) {
        this.airportHangars = airportHangars;
        rand = new Random();
    }

    @Override
    public void run() {
        
        while(true) {

            for (AirportHangar airportHangar : airportHangars) {
                for (HangarClient hangarClient : airportHangar.hangarClients) {
                    //System.out.println("[Monitor] Dispatching message from H" + hangarClient.airportHangar.hangarName);
                    hangarClient.sendMessage();
                }
            }

            int timeInterval = 1 + rand.nextInt(3);
            try {
                Thread.sleep(timeInterval * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}