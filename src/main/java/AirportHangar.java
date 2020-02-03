import javax.swing.DefaultListModel;
import javax.swing.JLabel;

import java.util.ArrayList;
import java.util.Map;

public class AirportHangar implements Runnable {

    private int airplaneCount;
    String hangarName;
    int serverPort;
    Map<String, Integer> clientMap;
    ArrayList<HangarClient> hangarClients = new ArrayList<>();

    JLabel jLabel;
    DefaultListModel<String> historyListModel;

    public AirportHangar(String hangarName, int airplaneCount, int serverPort, Map<String, Integer> clientMap, JLabel jLabel, DefaultListModel<String> historyListModel) {
        this.hangarName = hangarName;
        this.airplaneCount = airplaneCount;
        this.serverPort = serverPort;
        this.clientMap = clientMap;
        this.jLabel = jLabel;
        this.historyListModel = historyListModel;
        
        for (String client : this.clientMap.keySet()) {
            if (! this.hangarName.equals(client)) {
                HangarClient hangarClient = new HangarClient(this, this.clientMap.get(client), client);
                hangarClients.add(hangarClient);
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
        return airplaneCount;
    }

    public void setAirplaneCount(int planeCount) {
        this.airplaneCount = planeCount;
    }

}