package carpark;

public class Duration {
	
	private int hours;
	private int minutes;
	private int seconds;
	
	public Duration(int hours, int minutes, int seconds){
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
	}
	
	public int getHours(){
		return hours;
	}
	
	public int getMinutes(){
		return minutes;
	}
	
	public int getSeconds(){
		return seconds;
	}
	public int getTotalSeconds(){
		return seconds + 60*minutes + 60*60*hours;
	}
	
	public boolean isLongerThan(Duration d){
		if (getTotalSeconds() > d.getTotalSeconds()) return true;
		else return false;
	}
	
	public boolean isShorterThan(Duration d){
		if (getTotalSeconds() < d.getTotalSeconds()) return true;
		else return false;
	}
	
	public boolean equals(Duration d){
		if (getTotalSeconds() == d.getTotalSeconds()) return true;
		else return false;
	}
	
	public String toString(){
		
		return new String((hours>9?hours:"0"+hours)+":"+(minutes>9?minutes:"0"+minutes)+":"+(seconds>9?seconds:"0"+seconds));
	}
	
	public static Duration valueOf(String s){
		int first_col = s.indexOf(":");
		int last_col = s.indexOf(":", first_col+1);
		
		int hours = Integer.parseInt(s.substring(0,first_col));
		int minutes = Integer.parseInt(s.substring(first_col+1,last_col));
		int seconds = Integer.parseInt(s.substring(last_col+1,s.length()));
		
		return new Duration(hours, minutes, seconds);
	}
}
