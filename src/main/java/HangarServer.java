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
                startSnapshots();

                byte[] data = new byte[65535];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                serverSocket.receive(packet);
                
                ByteArrayInputStream baos = new ByteArrayInputStream(data);
                ObjectInputStream oos = new ObjectInputStream(baos);
                Message message = (Message)oos.readObject();

                String displayText = "";
                switch(message.getType()){
                    case TRANSFER:
                        displayText = "Transfer: H" + message.getSenderName() + " -> H" + this.airportHangar.hangarName + " (" + message.getAirplaneCount() + ")";
                        System.out.println(displayText);
                        this.airportHangar.historyListModel.addElement(displayText);

                        recordMessage(message);
                        this.airportHangar.increaseAirplaneCount(message.getAirplaneCount());
                        break;
                    case MARK:
                        displayText = "Mark: H" + message.getSenderName() + " -> H" + this.airportHangar.hangarName + " (ID:" + message.getSnapshotId() + ")";
                        System.out.println(displayText);
                        this.airportHangar.historyListModel.addElement(displayText);

                        // If the snapshot is already running, then stop recording and send final state if it is ready
                        // If it is not started yet, start a new snapshot and send markers to all peers
                        if(airportHangar.runningSnapshots.containsKey(message.getSnapshotId())){
                            Snapshot snapshot = airportHangar.runningSnapshots.get(message.getSnapshotId());
                            snapshot.finishRecording(message.getSenderName());
                            if(!airportHangar.hangarName.equals(snapshot.initiatorName) && snapshot.checkAllFinished()){
                                sendFinalState(snapshot);
                            }
                        }else{
                            Snapshot snapshot = startSnapshot(message.getSnapshotId(), message.getMarkInitiator());
                            snapshot.finishRecording(message.getSenderName());
                            sendMarkers(snapshot);
                        }
                        break;

                    case FINAL_STATE:
                        displayText = "Final State: H" + message.getSenderName() + " -> H" + this.airportHangar.hangarName + " (ID:" + message.getSnapshotId() + ")";
                        System.out.println(displayText);
                        this.airportHangar.historyListModel.addElement(displayText);

                        // Merge the final state and report it if all final states are received
                        Snapshot snapshot = airportHangar.runningSnapshots.get(message.getSnapshotId());
                        snapshot.mergeState(message.getSenderName(), message.getFinalState(), message.getRecordedMessages());
                        if(snapshot.allFinalStatesReceived()){
                            String state = snapshot.toString();
                            airportHangar.historyListModel.addElement(state);
                            System.out.println(state);
                        }
                        break;
                }
            }

            serverSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Start new snapshots when a request is placed in the new snapshot queue
    private void startSnapshots(){
        Integer snapshotId = airportHangar.snapshotInitiationRequests.poll();
        while(snapshotId !=null){
            Snapshot snapshot = startSnapshot(snapshotId, airportHangar.hangarName);
            sendMarkers(snapshot);

            snapshotId = airportHangar.snapshotInitiationRequests.poll();
        }
    }

    public Snapshot startSnapshot(int snapshotId, String initiator){
        Snapshot snapshot = new Snapshot(airportHangar, snapshotId, initiator);
        snapshot.recordState(airportHangar.getAirplaneCount());
        airportHangar.runningSnapshots.put(snapshotId, snapshot);
        return snapshot;
    }

    // Send marker to all other nodes
    private void sendMarkers(Snapshot snapshot){
        Message mark = null;
        for(HangarClient client : airportHangar.hangarClients){
            System.out.println("[H-" + airportHangar.hangarName + "] Queueing MARK message from H" + airportHangar.hangarName + " -> H" + client.serverName + "");
            mark = new Message(airportHangar.hangarName, snapshot.snapshotId, snapshot.initiatorName);
            client.channelBuffer.offer(mark);
        }
    }

    public void sendFinalState(Snapshot snapshot){
        Message finalState = new Message(airportHangar.hangarName, snapshot.snapshotId,  snapshot.recordedMessages, snapshot.finalStates.get(airportHangar.hangarName));
        System.out.println("[H-" + airportHangar.hangarName + "] Queueing FINAL_STATE message from H" + airportHangar.hangarName + " -> H" + snapshot.initiatorName + "");

        HangarClient initiator = airportHangar.hangarClientMap.get(snapshot.initiatorName);
        initiator.channelBuffer.offer(finalState);

    }

    private void recordMessage(Message message){
        for(Snapshot snapshot: airportHangar.runningSnapshots.values()){
            snapshot.recordMessage(message);
        }
    }
}