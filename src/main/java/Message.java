import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private int airplaneCount;
    private boolean isMarkerMessage;

    public Message(int airplaneCount, boolean isMarkerMessage) {
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