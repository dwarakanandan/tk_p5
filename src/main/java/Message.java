import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private MessageType type;

    // required for all messages
    private String senderName;

    // fields for TRANSFER message type
    private int airplaneCount;

    // fields for MARK messages
    private int snapshotId;
    private String markInitiator;

    //fields for FINAL_STATE messages
    private LinkedList<Message> recordedMessages;
    private int finalState;

    // TRANSFER constructor
    public Message(String senderName, int airplaneCount) {
        this.senderName = senderName;
        this.airplaneCount = airplaneCount;
        type = MessageType.TRANSFER;
    }


    // MARK constructor
    public Message(String senderName, int snapshotId, String markInitiator) {
        this.senderName = senderName;
        this.snapshotId = snapshotId;
        this.markInitiator = markInitiator;
        type = MessageType.MARK;
    }

    // FINAL_STATE constructor
    public Message(String senderName, int snapshotId, LinkedList<Message> recordedMessages, int finalState){
        this.senderName = senderName;
        this.snapshotId = snapshotId;
        this.recordedMessages = recordedMessages;
        this.finalState = finalState;
        type = MessageType.FINAL_STATE;
    }

    public int getSnapshotId() {
        return snapshotId;
    }

    public String getMarkInitiator() {
        return markInitiator;
    }

    public MessageType getType() {
        return type;
    }

    public LinkedList<Message> getRecordedMessages() {
        return recordedMessages;
    }

    public int getFinalState() {
        return finalState;
    }

    public int getAirplaneCount() {
        return this.airplaneCount;
    }

    public String getSenderName() {
        return senderName;
    }

}