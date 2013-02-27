package carpark;

import java.sql.Timestamp;

public class OccupancyStat {
	
	private Timestamp timestamp;
	private int vehicles;
	private int type0;
	private int type1;
	private int type2;
	private int type3;
	private int type4;
	
	public OccupancyStat(Timestamp timestamp, int vehicles, int type0, int type1, int type2, int type3, int type4) {
		this.timestamp = timestamp;
		this.vehicles = vehicles;
		this.type0 = type0;
		this.type1 = type1;
		this.type2 = type2;
		this.type3 = type3;
		this.type4 = type4;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public int getVehicles() {
		return vehicles;
	}

	public void setVehicles(int vehicles) {
		this.vehicles = vehicles;
	}

	public int getType0() {
		return type0;
	}

	public void setType0(int type0) {
		this.type0 = type0;
	}

	public int getType1() {
		return type1;
	}

	public void setType1(int type1) {
		this.type1 = type1;
	}

	public int getType2() {
		return type2;
	}

	public void setType2(int type2) {
		this.type2 = type2;
	}

	public int getType3() {
		return type3;
	}

	public void setType3(int type3) {
		this.type3 = type3;
	}

	public int getType4() {
		return type4;
	}

	public void setType4(int type4) {
		this.type4 = type4;
	}
}
