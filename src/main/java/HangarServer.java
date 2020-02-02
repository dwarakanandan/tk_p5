import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class HangarServer implements Runnable {

    AirportHangar airportHangar;
    boolean runServer = true;

    public HangarServer(AirportHangar airportHangar) {
        this.airportHangar = airportHangar;
    }

    public void run() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(this.airportHangar.serverPort);

            while(runServer) {
                byte[] data = new byte[65535];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                serverSocket.receive(packet);
                
                ByteArrayInputStream baos = new ByteArrayInputStream(data);
                ObjectInputStream oos = new ObjectInputStream(baos);
                Message message = (Message)oos.readObject();
                this.airportHangar.setAirplaneCount(this.airportHangar.getAirplaneCount() + message.getAirplaneCount());
                System.out.println("[H-"+this.airportHangar.hangarName+"] got " + message.getAirplaneCount());
            }

            serverSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}