import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;

public class Snapshot {


    AirportHangar hangar;
    int snapshotId;
    String initiatorName;
    LinkedList<Message> recordedMessages = new LinkedList<>();
    HashMap<String, Boolean> finishedRecording = new HashMap<>();
    HashMap<String, Integer> finalStates = new HashMap<>();

    public Snapshot(AirportHangar hangar, int snapshotId, String initiatorName){
        this.hangar = hangar;
        this.snapshotId = snapshotId;
        this.initiatorName = initiatorName;
        for(HangarClient otherHangar : hangar.hangarClients){
            finishedRecording.put(otherHangar.serverName, false);
        }
    }

    public void recordState(int state){
        finalStates.put(hangar.hangarName, state);
    }

    public boolean allFinalStatesReceived(){
        for(String client : finishedRecording.keySet()){
            if(!finalStates.containsKey(client)) return false;
        }
        return true;
    }

    public void mergeState(String fromClient, int clientState, LinkedList<Message> messages){
        finalStates.put(fromClient, clientState);
        recordedMessages.addAll(messages);
    }

    public void recordMessage(Message message){
        if(!finishedRecording.get(message.getSenderName())){
            recordedMessages.add(message);
        }
    }

    public void finishRecording(String clientName){
        finishedRecording.put(clientName, true);
    }

    public boolean checkAllFinished(){
        System.out.println("H" + hangar.hangarName + " checking finished");
        for(Boolean finished : finishedRecording.values()){
            System.out.println(finished);
            if(!finished) return false;
        }
        return true;
    }

    public String toString(){
        int totalPlanes = 0;
        String header = "[Snapshot-" + snapshotId + "]: ";
        String finalStatesStr = "Final states: ";
        for(String hangar : finalStates.keySet()){
            totalPlanes += finalStates.get(hangar);
            finalStatesStr += "H" + hangar + ": " + finalStates.get(hangar) + ", ";
        }
        String recordedMesssagesStr = "Recorded messages: ";
        for(Message message : recordedMessages){
            recordedMesssagesStr += "H" + message.getSenderName() + " -> H" + hangar.hangarName + " (" + message.getAirplaneCount() + "), ";
            totalPlanes += message.getAirplaneCount();
        }

        return header + "\n" + finalStatesStr + "\n" + recordedMesssagesStr + "\n Total Planes: " + totalPlanes;
    }

}

