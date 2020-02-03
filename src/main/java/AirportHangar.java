import javax.swing.DefaultListModel;
import javax.swing.JLabel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AirportHangar implements Runnable {

    private int airplaneCount;
    String hangarName;
    int serverPort;
    Map<String, Integer> clientMap;
    HashMap<String, ConcurrentLinkedQueue<Message>> outgoingMessageQueues = new HashMap<>();
    ArrayList<HangarClient> hangarClients = new ArrayList<>();
    Random rand;


    JLabel jLabel;
    DefaultListModel<String> historyListModel;

    public AirportHangar(String hangarName, int airplaneCount, int serverPort, Map<String, Integer> clientMap, JLabel jLabel, DefaultListModel<String> historyListModel) {
        this.hangarName = hangarName;
        this.airplaneCount = airplaneCount;
        this.serverPort = serverPort;
        this.clientMap = clientMap;
        this.jLabel = jLabel;
        this.historyListModel = historyListModel;
        this.rand = new Random();
        
        for (String client : this.clientMap.keySet()) {
            if (! this.hangarName.equals(client)) {
                HangarClient hangarClient = new HangarClient(this, this.clientMap.get(client), client);
                hangarClients.add(hangarClient);
                outgoingMessageQueues.put(client, new ConcurrentLinkedQueue<>());
            }
        }
    }

    @Override
    public void run() {
        new Thread(new PlaneShuffle(this)).start();
        new Thread(new HangarServer(this)).start();

        int timeInterval = 1 + rand.nextInt(4);
        while(true){
            try {
                Thread.sleep(timeInterval * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (getAirplaneCount() > 0) {
                int planeCount = 1 + rand.nextInt(5);

                if (planeCount > getAirplaneCount()) {
                    planeCount = getAirplaneCount();
                }

                HangarClient receiverClient = hangarClients.get(rand.nextInt(this.hangarClients.size()));
                Message message = new Message(hangarName, planeCount, false);
                outgoingMessageQueues.get(receiverClient.serverName).add(message);
                setAirplaneCount(getAirplaneCount() - planeCount);
            }

            timeInterval = 1 + rand.nextInt(4);
        }

    }

    public boolean areAllChannelsEmpty() {
        for (HangarClient hangarClient : hangarClients) {
            if (hangarClient.channelBuffer.size() > 0) {
                return false;
            }
        }
        return true;
    }

    public int getAirplaneCount() {
        return airplaneCount;
    }

    public void setAirplaneCount(int planeCount) {
        this.airplaneCount = planeCount;
    }

}