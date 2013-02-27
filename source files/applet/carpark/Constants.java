package carpark;

import java.awt.Rectangle;

/*
 * COORDINATES contains a list of the coordinates of the
 * parking lots on the map. The number of nodes must be NODES
 * and the ids must range from 0 to NODES-1
 */

public class Constants {
	public static final String[] STATUS = {"free", "occupied"};
	public static final int FREE = 0;
	public static final int OCCUPIED = 1;
	
	public static final String[] TYPE = {"motorcycle", "small car", "large car", "SUV", "truck"};
	
	public static final String[] DIRECTION = {"inwards", "outwards"};
	public static final int IN = 0;
	public static final int OUT = 1;
	
	public static final int NO_NODE_SELECTED = -1;
	public static final int NODES = 8;
	
	public static final String IMG_FILE = "parking_lots.JPG";
	public static final Rectangle[] COORDINATES = {
		new Rectangle(30,10,50,90),
		new Rectangle(102,10,50,90),
		new Rectangle(170,10,50,90),
		new Rectangle(240,10,50,90),
		new Rectangle(30,125,50,90),
		new Rectangle(102,125,50,90),
		new Rectangle(170,125,50,90),
		new Rectangle(240,125,50,90)
	};
}
