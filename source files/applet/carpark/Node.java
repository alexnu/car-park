package carpark;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Node {

	private int nodeId;
	private int count;
	private int status;
	private int type;
	private double battery;
	private Timestamp lastContact;

	public Node(int nodeId, int count, int status, int type, double battery, Timestamp lastContact){
		this.nodeId = nodeId;
		this.count = count;
		this.status = status;
		this.type = type;
		this.battery = battery;
		this.lastContact = lastContact;
	}

	public int getNodeId(){
		return nodeId;
	}

	public int getCount(){
		return count;
	}

	public void setCount(int count){
		this.count = count;
	}

	public int getStatus(){
		return status;
	}

	public void setStatus(int status){
		this.status = status;
	}
	
	public int getType(){
		return type;
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public double getBattery(){
		return battery;
	}
	
	public void setBattery(double battery){
		this.battery = battery;
	}

	public Timestamp getLastContact(){
		return lastContact;
	}
	
	public String getContactString(){
		Date contact = new Date(lastContact.getTime());

		SimpleDateFormat out =
            new SimpleDateFormat("HH:mm:ss");

		return out.format(contact).toString();
	}

	public void setLastContact(Timestamp lastContact){
		this.lastContact = lastContact;
	}
}