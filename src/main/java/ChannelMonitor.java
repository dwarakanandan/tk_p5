import java.util.ArrayList;
import java.util.Random;

import javax.swing.DefaultListModel;

public class ChannelMonitor implements Runnable {

    private ArrayList<AirportHangar> airportHangars;
    private DefaultListModel<String> historyListModel;
    private Random rand;

    private int hangarCount;

    public ChannelMonitor(ArrayList<AirportHangar> airportHangars, DefaultListModel<String> historyListModel) {
        this.airportHangars = airportHangars;
        this.historyListModel = historyListModel;
        this.hangarCount = airportHangars.size();
        rand = new Random();
    }

    private int getOutHanger() {
        int outHangar = rand.nextInt(this.hangarCount);
        while(this.airportHangars.get(outHangar).getOutputChannel().size() == 0) {
            outHangar = rand.nextInt(this.hangarCount);
        }
        return outHangar;
    }

    private int getInHanger(int outHangar) {
        int inHangar = rand.nextInt(this.hangarCount);
        while(inHangar == outHangar) {
            inHangar = rand.nextInt(this.hangarCount);
        }
        return inHangar;
    }

    @Override
    public void run() {
        
        while(true) {
            int outHangar = getOutHanger();
            int outPlaneCount = this.airportHangars.get(outHangar).getOutputChannel().poll();

            int inHangar = getInHanger(outHangar);
            this.airportHangars.get(inHangar).getInputChannel().offer(outPlaneCount);
            String displayText = "Transfer: H" + (outHangar+1) + " -> H" + (inHangar+1) + " (" + outPlaneCount + ")";
            System.out.println(displayText);
            historyListModel.addElement(displayText);

            int timeInterval = 1 + rand.nextInt(3);
            try {
                Thread.sleep(timeInterval * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}