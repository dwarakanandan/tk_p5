import java.util.ArrayList;
import java.util.Random;

public class ChannelMonitor implements Runnable {

    private ArrayList<AirportHangar> airportHangars;
    private Random rand;

    public ChannelMonitor(ArrayList<AirportHangar> airportHangars) {
        this.airportHangars = airportHangars;
        rand = new Random();
    }

    HangarClient getClientToDispatch() {
        HangarClient hangarClient = null;
        AirportHangar airportHangar = null;

        do {
            int hangar = rand.nextInt(this.airportHangars.size());
            airportHangar = this.airportHangars.get(hangar);
        } while(airportHangar.areAllChannelsEmpty());

        do {
            int client = rand.nextInt(airportHangar.hangarClients.size());
            hangarClient = airportHangar.hangarClients.get(client);
        } while(hangarClient.channelBuffer.isEmpty());

        return hangarClient;
    }

    @Override
    public void run() {
        
        while(true) {

            HangarClient hangarClient = getClientToDispatch();
            System.out.println("[Monitor] Dispatching message from H" + hangarClient.airportHangar.hangarName);
            hangarClient.sendMessage();

            int timeInterval = 1 + rand.nextInt(3);
            try {
                Thread.sleep(timeInterval * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}