package carpark;

import java.sql.*;
import java.util.Date;

/**
 * This class was created for testing the server in the absence of actual sensor nodes.
 * 
 * @author nafasal
 */
public class EventGenerator {
	
	// determines the frequency that messages are sent to database
	private Connection conn;
	private String sqlInsertStatement;
	private String entranceInsertStatement;
	private PreparedStatement pstmt;
	private Node[] nodes;
	Timestamp now;
	
	public EventGenerator() {
		
		now = new Timestamp(new Date().getTime() - 2*24*3600000);
		
		try {
			// connect to database
			String url = "jdbc:mysql://localhost/carpark";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, "guest", "guest");
			System.out.println("Database connection established");
			sqlInsertStatement = "CALL reset_database(?)";
			pstmt = conn.prepareStatement(sqlInsertStatement);
			pstmt.setTimestamp(1, now);
			pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			System.out.println("error1: " + e.getMessage());
			System.exit(1);
		}
		
		nodes = new Node[Constants.NODES];
		for (int i = 0; i < Constants.NODES; i++)
			nodes[i] = new Node(i, 0, Constants.FREE, Constants.FREE, 1, now);

		while(now.before(new Date())) {
			// update timestamp
			now = new Timestamp( (new Date(now.getTime())).getTime() + 60000+ (int) (Math.random() * 1000));
			// select a random node
			int i = (int) (Math.random() * Constants.NODES);

			// node status is changed with 60% probability
			if (Math.random() > 0.6){
				int newStatus = (int) (Math.random() * 2);
				if (nodes[i].getStatus() == Constants.FREE && newStatus == Constants.OCCUPIED){
					nodes[i].setType((int) (Math.random() * Constants.TYPE.length));
					entranceInsertStatement = "INSERT INTO entrance VALUES (?, "+nodes[i].getType()+", "+(int) (Math.random()*60)+", 0)";
				} else if (nodes[i].getStatus() == Constants.OCCUPIED && newStatus == Constants.FREE){
					entranceInsertStatement = "INSERT INTO entrance VALUES (?, "+nodes[i].getType()+", "+(int) (Math.random()*60)+", 1)";
				}
				nodes[i].setStatus(newStatus);
			}
			nodes[i].setLastContact(now);
			nodes[i].setCount(nodes[i].getCount() + 1);

			// update DATABASE
			try {
				Thread.sleep(10);
				System.out.println("INSERT INTO event VALUES ("+nodes[i].getNodeId()+", "
						+nodes[i].getCount()+", "+nodes[i].getStatus()+", "
						+nodes[i].getType()+", "+nodes[i].getBattery()+", "
						+nodes[i].getLastContact()+")");
				
				sqlInsertStatement = "INSERT INTO event VALUES (?, ?, ?, ?, ?, ?)";
				pstmt = conn.prepareStatement(sqlInsertStatement);
				pstmt.setInt(1, nodes[i].getNodeId());
				pstmt.setInt(2, nodes[i].getCount());
				pstmt.setInt(3, nodes[i].getStatus());
				pstmt.setInt(4, nodes[i].getType());
				pstmt.setDouble(5, nodes[i].getBattery());
				pstmt.setTimestamp(6, nodes[i].getLastContact());
				pstmt.executeUpdate();
				
				if (entranceInsertStatement != null){
					pstmt = conn.prepareStatement(entranceInsertStatement);
					pstmt.setTimestamp(1, now);
					pstmt.executeUpdate();
				}
				pstmt.close();
				
				// sleep for a random period of time
				//Thread.sleep((int) (Math.random() * FREQUENCY));
			} catch (Exception e) {
				System.out.println("error2: " + e.getMessage());
				System.exit(1);
			}
			entranceInsertStatement = null;
		}
	}
	
	public static void main(String[] args) {
		new EventGenerator();
	}
}