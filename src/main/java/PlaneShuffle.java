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
            timeInterval = 1 + rand.nextInt(4);
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

        int receiverHangarNum = rand.nextInt(this.airportHangar.hangarClients.size());
        HangarClient receiverHanger = this.airportHangar.hangarClients.get(receiverHangarNum);
        Message message = new Message(airportHangar.hangarName, planeCount);
        receiverHanger.channelBuffer.offer(message);
        this.airportHangar.increaseAirplaneCount(-planeCount);
        System.out.println("[H-"+this.airportHangar.hangarName+"] Queueing TRANSFER message from H"+this.airportHangar.hangarName + " -> H"+(receiverHangarNum+1));
    }
}