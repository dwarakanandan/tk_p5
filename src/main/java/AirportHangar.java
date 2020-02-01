import java.util.concurrent.*;

import javax.swing.JLabel;

import java.util.Random;

public class AirportHangar implements Runnable {

    private int airplaneCount;
    private String hangarName;
    private JLabel jLabel;
    private ConcurrentLinkedQueue<Integer> inputQueue = new ConcurrentLinkedQueue<Integer>();
    private ConcurrentLinkedQueue<Integer> outputQueue = new ConcurrentLinkedQueue<Integer>();

    public AirportHangar(String hangarName, int airplaneCount, JLabel jLabel) {
        this.hangarName = hangarName;
        this.airplaneCount = airplaneCount;
        this.jLabel = jLabel;
    }

    @Override
    public void run() {
        new Thread(new PlaneShuffle(this)).start();
        monitorInputChannel();
    }

    public ConcurrentLinkedQueue<Integer> getInputChannel() {
        return inputQueue;
    }

    public ConcurrentLinkedQueue<Integer> getOutputChannel() {
        return outputQueue;
    }

    public int getAirplaneCount() {
        return airplaneCount;
    }

    public void setAirplaneCount(int planeCount) {
        this.airplaneCount = planeCount;
    }

    private void monitorInputChannel() {
        while (true) {
            if (this.getInputChannel().size() != 0) {
                int planeCount = this.getInputChannel().poll();
                this.setAirplaneCount(this.getAirplaneCount() + planeCount);
                this.jLabel.setText(this.hangarName+"(#"+this.getAirplaneCount()+")");
                System.out.println("[" + hangarName + "]" + " Received " + planeCount + " planes");
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class PlaneShuffle implements Runnable {

        AirportHangar airportHangar;
        Random rand;

        public PlaneShuffle(AirportHangar airportHangar) {
            this.airportHangar = airportHangar;
            rand = new Random();
        }

        @Override
        public void run() {
            
            int timeInterval = 1 + rand.nextInt(4);
            while (true) {
                shufflePlanes();
                try {
                    Thread.sleep(timeInterval * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void shufflePlanes() {
            if (this.airportHangar.getAirplaneCount() == 0) {
                return;
            }

            int planeCount = 1 + rand.nextInt(5);

            if (planeCount > this.airportHangar.getAirplaneCount()) {
                planeCount = this.airportHangar.getAirplaneCount();
            }

            this.airportHangar.setAirplaneCount(this.airportHangar.getAirplaneCount() - planeCount);
            this.airportHangar.jLabel.setText(this.airportHangar.hangarName+" (#"+this.airportHangar.getAirplaneCount()+")");
            this.airportHangar.getOutputChannel().offer(planeCount);
            System.out.println("[" + airportHangar.hangarName + "]" + " Sent " + planeCount + " planes");
        }
    }

}