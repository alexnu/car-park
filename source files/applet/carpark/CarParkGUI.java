package carpark;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.*;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This is the main class of the application.
 * 
 * @author nafasal
 */
public class CarParkGUI extends JApplet implements Runnable, MouseListener, ActionListener, ListSelectionListener {

	private URL imageSrc;
	private URL urlServlet;
	private ParkingMap map;
	protected Node[] nodes = new Node[Constants.NODES];
	private EntranceStat[] entries;
	private JEditorPane infoArea;
	private JEditorPane entranceArea;
	private JList list;
	private DefaultListModel listModel;
	private int nodeSelected;
	private String onlineSince;
	private Timestamp latestEntry;
	private JPanel panel2;
	private JPanel chartPanel;
	private JComboBox graphList;
	private JComboBox paramList;
	private JButton showButton;
	
	private static final int REFRESH = 1000;

	public void init() {
		
		try {
            imageSrc = new URL(getCodeBase(), Constants.IMG_FILE);
            urlServlet = new URL(getCodeBase(), "echo");
        } catch (MalformedURLException e) {
        	System.out.println("Image-URL could not be read");
        }
		
        FeedParser.setURL(urlServlet);
		nodes = FeedParser.getNodes();
		onlineSince = FeedParser.getOnlineSince();
		latestEntry = FeedParser.getLatestEntry();
		nodeSelected = Constants.NO_NODE_SELECTED;
		
		// create first panel
		JPanel panel1 = new JPanel();
		
		panel1.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10,10,10,10);
		
        map = new ParkingMap(imageSrc, nodes);
		panel1.addMouseListener(this);
		
		infoArea = new JEditorPane();
		infoArea.setContentType("text/html");
		infoArea.setEditable(false);
		infoArea.setPreferredSize(new Dimension(250,map.getPreferredSize().height));
        infoArea.setMinimumSize(new Dimension(250,map.getPreferredSize().height));
		
		updateInfo();
		
		JPanel text1 = new JPanel(new GridLayout(1, 1), false);
        JLabel label1 = new JLabel("Parking Map", JLabel.CENTER);
        text1.add(label1);
        text1.setBorder(BorderFactory.createLineBorder(Color.black));
        text1.setPreferredSize(new Dimension(map.getPreferredSize().width, 50));
        text1.setMinimumSize(new Dimension(map.getPreferredSize().width, 50));

		c.gridx = 0;
		c.gridy = 0;
		panel1.add(text1, c);
		
		JPanel text2 = new JPanel(new GridLayout(1, 1), false);
        JLabel label2 = new JLabel("Info", JLabel.CENTER);
        text2.add(label2);
        text2.setBorder(BorderFactory.createLineBorder(Color.black));
        text2.setPreferredSize(new Dimension(250,50));
        text2.setMinimumSize(new Dimension(250,50));

		c.gridx = 1;
		c.gridy = 0;
		panel1.add(text2, c);
		
		c.gridx = 0;
		c.gridy = 1;
		panel1.add(map, c);

		c.gridx = 1;
		c.gridy = 1;
		panel1.add(infoArea, c);
		
		// create second panel
		panel2 = new JPanel();
		panel2.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 30));
		
		//Create the combo boxes
		String[] graphStrings = {"Choose type of chart", "Occupancy Chart", "Occupancy By Type", "Visit-Time Chart", "Type Chart"};
		graphList = new JComboBox(graphStrings);
		graphList.setSelectedIndex(0);
		graphList.addActionListener(this);
		panel2.add(graphList);
		
		String[] paramStrings = {"Choose parameter"};
		paramList = new JComboBox(paramStrings);
		panel2.add(paramList);
		
		showButton = new JButton("Show");
		showButton.addActionListener(this);
		panel2.add(showButton);
		
		chartPanel = new OccupancyChart(new Timestamp(latestEntry.getTime() - 2*3600000));
		panel2.add(chartPanel);
		
		//create third panel
		JPanel panel3 = new JPanel();
		panel3.setLayout(new GridBagLayout());
		
		//create first label
		JPanel text3 = new JPanel(new GridLayout(1, 1), false);
        JLabel label3 = new JLabel("Latest Entries", JLabel.CENTER);
        text3.add(label3);
        text3.setBorder(BorderFactory.createLineBorder(Color.black));
        text3.setPreferredSize(new Dimension(map.getPreferredSize().width, 50));
        text3.setMinimumSize(new Dimension(map.getPreferredSize().width, 50));

		c.gridx = 0;
		c.gridy = 0;
		panel3.add(text3, c);
		
		//create second label
		JPanel text4 = new JPanel(new GridLayout(1, 1), false);
        JLabel label4 = new JLabel("Info", JLabel.CENTER);
        text4.add(label4);
        text4.setBorder(BorderFactory.createLineBorder(Color.black));
        text4.setPreferredSize(new Dimension(250,50));
        text4.setMinimumSize(new Dimension(250,50));

		c.gridx = 1;
		c.gridy = 0;
		panel3.add(text4, c);
		
		//create list
		listModel = new DefaultListModel();

        //and put it in a scroll pane.
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);
        listScrollPane.setPreferredSize(new Dimension(map.getPreferredSize().width,map.getPreferredSize().height));
        
        updateEntries();

		c.gridx = 0;
		c.gridy = 1;
		panel3.add(listScrollPane, c);

		//create text area
		entranceArea = new JEditorPane();
		entranceArea.setContentType("text/html");
		entranceArea.setEditable(false);
		entranceArea.setPreferredSize(new Dimension(250,map.getPreferredSize().height));
		entranceArea.setMinimumSize(new Dimension(250,map.getPreferredSize().height));
		entranceArea.setText("Select an entry to view more info");
        
		c.gridx = 1;
		c.gridy = 1;
		panel3.add(entranceArea, c);
		
		//join them all in tabs
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Nodes", null, panel1, "Monitor sensors real-time");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.addTab("Entrance", null, panel3, "Monitor entrance");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.addTab("Statistics", null, panel2, "View network statistics");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

		add(tabbedPane);
	}

	public void start() {
		// dispatch a new thread which will update information
		new Thread(this).start();
	}

	public void run() {
		while (true) {
			try {
				nodes = FeedParser.getNodes();
				map.updateNodes(nodes);
				map.repaint();
				updateInfo();
				Thread.sleep(REFRESH);
			} catch (Exception e) {
				System.out.println("Error: "+e.getMessage());
			}
		}
	}
	
	private void updateInfo(){
		if (nodeSelected == Constants.NO_NODE_SELECTED){
			int[] types = FeedParser.getTypeStats();
			int occupied=types[0]+types[1]+types[2]+types[3]+types[4];
			
			infoArea.setText("<html><table><tr><td>Number of Nodes:</td><td><b>"+Constants.NODES+
					"</b></td></tr><tr><td>Occupied:</td><td><b>"+occupied+
					"</b></td></tr><tr><td>"+Constants.TYPE[0]+"s:</td><td><b>"+types[0]+
					"</b></td></tr><tr><td>"+Constants.TYPE[1]+"s:</td><td><b>"+types[1]+
					"</b></td></tr><tr><td>"+Constants.TYPE[2]+"s:</td><td><b>"+types[2]+
					"</b></td></tr><tr><td>"+Constants.TYPE[3]+"s:</td><td><b>"+types[3]+
					"</b></td></tr><tr><td>"+Constants.TYPE[4]+"s:</td><td><b>"+types[4]+
					"</b></td></tr><tr><td>Online Since:</td><td><b>"+onlineSince+
					"</b></td></tr></table></html>");
		} else {
			String text = "<html><table><tr><td>Node ID: </td><td><b>"+nodeSelected+
			"</b></td></tr><tr><td>Status: </td><td><b>"+Constants.STATUS[nodes[nodeSelected].getStatus()]+
			"</b></td></tr><tr><td>Type: </td><td><b>"
			+(nodes[nodeSelected].getStatus()==Constants.OCCUPIED?Constants.TYPE[nodes[nodeSelected].getType()]:"null")+
			"</b></td></tr><tr><td>Battery: </td><td><b>"+(int)nodes[nodeSelected].getBattery()*100+"%"+
			"</b></td></tr><tr><td>Last Contact: </td><td><b>"+nodes[nodeSelected].getContactString()+"</b></td></tr>";
			
			if (( (new Date()).getTime() - nodes[nodeSelected].getLastContact().getTime() )/1000 > 60)
				text = text + "<tr><th colspan='2'>ATTENTION: this node hasn't contacted for a long time</th></tr>";
			text = text + "</table></html>";
			infoArea.setText(text);
		}
	}
	
	private void updateEntries(){
		entries = FeedParser.getEntranceStats();
		
		for (int i=0;i<entries.length;i++){
			listModel.addElement(entries[i].getTimestampString());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getPoint().x - map.getLocation().x;
		int y = e.getPoint().y - map.getLocation().y;
		Point relativeLocation = new Point(x, y);
		
		boolean valid = false;
		for (int i=0; i < Constants.NODES; i++){
			if (Constants.COORDINATES[i].contains(relativeLocation)){
				map.changeFocus(i);
				nodeSelected = i;
				updateInfo();
				valid = true;
			}
		}
		if (!valid){
			map.changeFocus(Constants.NO_NODE_SELECTED);
			nodeSelected = Constants.NO_NODE_SELECTED;
			updateInfo();
		}
		map.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == graphList) {
			if (graphList.getSelectedIndex() == 1) {
				paramList.removeAllItems();
				paramList.addItem("Choose range");
				paramList.addItem("Last 2 hrs");
				paramList.addItem("Last 12 hrs");
				paramList.addItem("Last 24 hrs");
			} else if (graphList.getSelectedIndex() == 2) {
				paramList.removeAllItems();
				paramList.addItem("Choose range");
				paramList.addItem("Last 2 hrs");
				paramList.addItem("Last 12 hrs");
				paramList.addItem("Last 24 hrs");
			} else if (graphList.getSelectedIndex() == 3) {
				paramList.removeAllItems();
				paramList.addItem("Choose interval");
				paramList.addItem("20 minutes");
				paramList.addItem("40 minutes");
				paramList.addItem("60 minutes");
			} else if (graphList.getSelectedIndex() == 4) {
				paramList.removeAllItems();
				paramList.addItem("No Parameter");
			} else {
				paramList.removeAllItems();
				paramList.addItem("Choose parameter");
			}
		} else if (e.getSource() == showButton && graphList.getSelectedIndex() != 0) {
			panel2.remove(chartPanel);
			
			if (graphList.getSelectedIndex() == 1) {
				if (paramList.getSelectedIndex() == 1)
					chartPanel = new OccupancyChart(new Timestamp(latestEntry.getTime() - 2*3600000));
				else if (paramList.getSelectedIndex() == 2)
					chartPanel = new OccupancyChart(new Timestamp(latestEntry.getTime() - 12*3600000));
				else if (paramList.getSelectedIndex() == 3)
					chartPanel = new OccupancyChart(new Timestamp(latestEntry.getTime() - 24*3600000));
			} else if (graphList.getSelectedIndex() == 2) {
				if (paramList.getSelectedIndex() == 1)
					chartPanel = new OccupancyByTypeChart(new Timestamp(latestEntry.getTime() - 2*3600000));
				else if (paramList.getSelectedIndex() == 2)
					chartPanel = new OccupancyByTypeChart(new Timestamp(latestEntry.getTime() - 12*3600000));
				else if (paramList.getSelectedIndex() == 3)
					chartPanel = new OccupancyByTypeChart(new Timestamp(latestEntry.getTime() - 24*3600000));
			} else if (graphList.getSelectedIndex() == 3) {
				if (paramList.getSelectedIndex() == 1)
					chartPanel = new StayTimeChart(20);
				else if (paramList.getSelectedIndex() == 2)
					chartPanel = new StayTimeChart(40);
				else if (paramList.getSelectedIndex() == 3)
					chartPanel = new StayTimeChart(60);
			} else if (graphList.getSelectedIndex() == 4) {
				chartPanel = new TypeChart();
			}
			panel2.add(chartPanel);
	        panel2.validate();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			if (list.getSelectedIndex() == -1) {
				// No selection, reset entrance area.
				entranceArea.setText("Select an entry to view more info");

			} else {
				// Selection, display entry info.
				entranceArea.setText("<html><table><tr><td>Timestamp: </td><td><b>"+entries[list.getSelectedIndex()].getTimestampString()+
				"</b></td></tr><tr><td>Type: </td><td><b>"+Constants.TYPE[entries[list.getSelectedIndex()].getType()]+
				"</b></td></tr><tr><td>Speed: </td><td><b>"+entries[list.getSelectedIndex()].getSpeed()+
				" kmh</b></td></tr><tr><td>Direction: </td><td><b>"+Constants.DIRECTION[entries[list.getSelectedIndex()].getDirection()]+
				"</b></td></tr></table></html>");
			}
		}
	}
}