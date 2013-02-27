package carpark;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EntranceStat {
	
	private Timestamp timestamp;
	private int type;
	private int speed;
	private int direction;
	
	public EntranceStat(Timestamp timestamp, int type, int speed, int direction) {
		this.timestamp = timestamp;
		this.type = type;
		this.speed = speed;
		this.direction = direction;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	public String getTimestampString(){
		Date contact = new Date(timestamp.getTime());

		SimpleDateFormat out =
            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		return out.format(contact).toString();
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
}
