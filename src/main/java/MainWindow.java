import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private DefaultListModel<String> historyListModel;
	private JList<String> historyList;

	private JLabel snapshot1Label;
	private JButton snapshot1Button;

	private JLabel snapshot2Label;
	private JButton snapshot2Button;

	private JLabel snapshot3Label;
	private JButton snapshot3Button;
	
	public static void main(String[] args) {
		MainWindow mainWindow = new MainWindow();
		mainWindow.setVisible(true);

		Map<String, Integer> clientMap = new HashMap<>();
		clientMap.put("1", 8001);
		clientMap.put("2", 8002);
		clientMap.put("3", 8003);

		AirportHangar airportHangar1 = new AirportHangar("1", 10, 8001, clientMap, mainWindow.snapshot1Label, mainWindow.historyListModel);
		AirportHangar airportHangar2 = new AirportHangar("2", 10, 8002, clientMap, mainWindow.snapshot2Label, mainWindow.historyListModel);
		AirportHangar airportHangar3 = new AirportHangar("3", 10, 8003, clientMap, mainWindow.snapshot3Label, mainWindow.historyListModel);

		mainWindow.snapshot1Button.addActionListener(airportHangar1);
		mainWindow.snapshot2Button.addActionListener(airportHangar2);
		mainWindow.snapshot3Button.addActionListener(airportHangar3);

		ArrayList<AirportHangar> airportHangars = new ArrayList<>();
		airportHangars.add(airportHangar1);
		airportHangars.add(airportHangar2);
		airportHangars.add(airportHangar3);

		ChannelMonitor channelMonitor = new ChannelMonitor(airportHangars);

		Thread hangThread1 = new Thread(airportHangar1);
		Thread hangThread2 = new Thread(airportHangar2);
		Thread hangThread3 = new Thread(airportHangar3);
		Thread channelMoitorThread = new Thread(channelMonitor);

		hangThread1.start();
		hangThread2.start();
		hangThread3.start();

		channelMoitorThread.start();

		while(true){
			mainWindow.snapshot1Label.setText("Hangar 1 (#" + airportHangar1.getAirplaneCount() + ")");
			mainWindow.snapshot2Label.setText("Hangar 2 (#" + airportHangar2.getAirplaneCount() + ")");
			mainWindow.snapshot3Label.setText("Hangar 3 (#" + airportHangar3.getAirplaneCount() + ")");

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public MainWindow() {
		setSize(400, 300);
		setTitle("TK1-EX5");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// history list
		historyListModel = new DefaultListModel<String>();
		historyList = new JList<String>(historyListModel);
		historyList.setAutoscrolls(true);
		
		JScrollPane historyScroll = new JScrollPane(historyList);
		add(historyScroll, BorderLayout.CENTER);

		// slide panel
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new GridLayout(3, 1));

		// snapshot 1
		snapshot1Label = new JLabel("Hangar 1 (#0)");
		snapshot1Button = new JButton("Snapshot");

		JPanel snapshot1 = new JPanel();
		snapshot1.setLayout(new GridLayout(2, 1));
		snapshot1.add(snapshot1Label);
		snapshot1.add(snapshot1Button);
		sidePanel.add(snapshot1);

		// snapshot 2
		snapshot2Label = new JLabel("Hangar 2 (#0)");
		snapshot2Button = new JButton("Snapshot");

		JPanel snapshot2 = new JPanel();
		snapshot2.setLayout(new GridLayout(2, 1));
		snapshot2.add(snapshot2Label);
		snapshot2.add(snapshot2Button);
		sidePanel.add(snapshot2);

		// snapshot 3
		snapshot3Label = new JLabel("Hangar 3 (#0)");
		snapshot3Button = new JButton("Snapshot");

		JPanel snapshot3 = new JPanel();
		snapshot3.setLayout(new GridLayout(2, 1));
		snapshot3.add(snapshot3Label);
		snapshot3.add(snapshot3Button);
		sidePanel.add(snapshot3);

		add(sidePanel, BorderLayout.EAST);
	}

}
