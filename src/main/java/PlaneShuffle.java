import java.util.Random;

public class PlaneShuffle implements Runnable {

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

        Message message = new Message(planeCount, false);
        int receiverHanger = rand.nextInt(this.airportHangar.hangarClients.size());
        this.airportHangar.hangarClients.get(receiverHanger).channelBuffer.offer(message);
        this.airportHangar.setAirplaneCount(this.airportHangar.getAirplaneCount() - planeCount);
    }
}