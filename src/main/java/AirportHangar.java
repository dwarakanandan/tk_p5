import javax.swing.DefaultListModel;
import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class AirportHangar implements Runnable, ActionListener {

    private AtomicInteger airplaneCount;
    String hangarName;
    int serverPort;
    Map<String, Integer> clientMap;
    ArrayList<HangarClient> hangarClients = new ArrayList<>();
    HashMap<String, HangarClient> hangarClientMap = new HashMap<>();
    HashMap<Integer, Snapshot> runningSnapshots = new HashMap<>();
    Random rand = new Random();
    ConcurrentLinkedQueue<Integer> snapshotInitiationRequests = new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<String> historyQueue;


    public AirportHangar(String hangarName, int airplaneCount, int serverPort, Map<String, Integer> clientMap, ConcurrentLinkedQueue<String> historyQueue) {
        this.hangarName = hangarName;
        this.airplaneCount = new AtomicInteger(airplaneCount);
        this.serverPort = serverPort;
        this.clientMap = clientMap;
        this.historyQueue = historyQueue;
        
        for (String client : this.clientMap.keySet()) {
            if (! this.hangarName.equals(client)) {
                HangarClient hangarClient = new HangarClient(this, this.clientMap.get(client), client);
                hangarClients.add(hangarClient);
                hangarClientMap.put(client, hangarClient);
            }
        }
    }

    @Override
    public void run() {
        new Thread(new PlaneShuffle(this)).start();
        new Thread(new HangarServer(this)).start();
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
        return airplaneCount.get();
    }

    public void setAirplaneCount(int planeCount) {
        this.airplaneCount.set(planeCount);
    }

    public void increaseAirplaneCount(int amount){
        airplaneCount.addAndGet(amount);
    }

    // Generate a random snapshot ID and place it in the snapshot queue
    public void startSnapshot(){
        int snapshotId = rand.nextInt(10000);
        String displayText = "Starting [Snapshot-" + snapshotId + "] on [H-" + hangarName + "]";
        System.out.println(displayText);
        historyQueue.offer(displayText);

        snapshotInitiationRequests.offer(snapshotId);
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        startSnapshot();
    }
}