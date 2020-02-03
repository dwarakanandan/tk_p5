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
        
        int timeInterval = 1 + rand.nextInt(3);
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

        HangarClient receiverClient = hangarClients.get(rand.nextInt(this.hangarClients.size()));
        Message message = airportHangar.outgoingMessageQueues.get(receiverClient.serverName).poll();
        if(message){
            this.airportHangar.hangarClients.get(receiverHanger).channelBuffer.offer(message);
            System.out.println("[H-"+this.airportHangar.hangarName+"] Queueing message from H"+this.airportHangar.hangarName + " -> H"+(receiverHanger+1));
        }
    }
}