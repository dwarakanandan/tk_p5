import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private String senderName;
    private int airplaneCount;
    private boolean isMarkerMessage;

    public Message(String senderName, int airplaneCount, boolean isMarkerMessage) {
        this.senderName = senderName;
        this.airplaneCount = airplaneCount;
        this.isMarkerMessage = isMarkerMessage;
    }

    public int getAirplaneCount() {
        return this.airplaneCount;
    }

    public boolean isMarkerMessage() {
        return this.isMarkerMessage;
    }
}