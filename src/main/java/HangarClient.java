import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HangarClient {

    AirportHangar airportHangar;
    String serverName;
    int port;
    ConcurrentLinkedQueue<Message> channelBuffer;
    
    public HangarClient(AirportHangar airportHangar, int port, String serverName) {
        this.airportHangar = airportHangar;
        this.port = port;
        this.serverName = serverName;
        channelBuffer = new ConcurrentLinkedQueue<>();
    }

    public void sendMessage() {
        Message message = channelBuffer.poll();
        if (message!= null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(message);
                oos.flush();
                byte[] data = baos.toByteArray();
                DatagramSocket socket = new DatagramSocket();
                InetAddress client = InetAddress.getLocalHost();
                DatagramPacket packet = new DatagramPacket(data, data.length, client, port);
                socket.send(packet);
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}