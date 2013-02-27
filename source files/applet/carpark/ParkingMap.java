package carpark;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

public class ParkingMap extends Component {

    private BufferedImage bi;
    int w, h;
    private Node[] nodes;
    private int focus = -1;
    
    public ParkingMap(URL imageSrc, Node[] nodes) {
    	this.nodes = nodes;
        try {
            bi = ImageIO.read(imageSrc);
            w = bi.getWidth(null);
            h = bi.getHeight(null);
        } catch (IOException e) {
            System.out.println("Image could not be read");
            System.exit(1);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }
    
    public void changeFocus(int newFocus){
    	focus = newFocus;
    }
    
    public void updateNodes(Node[] nodes){
    	this.nodes = nodes;
    }

    public void paint(Graphics g) {
    	Graphics2D g2 = (Graphics2D) g;

		g2.drawImage(bi, 0, 0, null);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				0.7f);
		g2.setComposite(ac);

		for (int i = 0; i < Constants.NODES; i++) {
			//draw a rectangle on each spot
			switch (nodes[i].getStatus()) {
			case Constants.FREE:
				g2.setColor(Color.green);
				break;
			case Constants.OCCUPIED:
				g2.setColor(Color.red);
				break;
			}
			if (focus == i){
				ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
				g2.setComposite(ac);
			}
			
			g2.fill(Constants.COORDINATES[i]);
			
			ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
			g2.setComposite(ac);
			
			//draw a small circle
			g2.setColor(Color.white);
			g2.fillOval(Constants.COORDINATES[i].x + 15, Constants.COORDINATES[i].y + 35, 20, 20);

			//draw a text with each node's ID inside the circle
			g2.setColor(Color.black);
	        g2.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 12));
	        g2.drawString(Integer.toString(i), Constants.COORDINATES[i].x + 22, Constants.COORDINATES[i].y + 50);
		}
    }
}
