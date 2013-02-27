package carpark;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public final class FeedParser {
	
	private static URL urlServlet;
	
	public static void setURL(URL url){
		urlServlet = url;
	}
	
	public static Node[] getNodes(){
		
		ArrayList<Node> nodeList = new ArrayList<Node>();
		
		try {
			// setup connection
			URLConnection con = urlServlet.openConnection();

			// set properties
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type",
					"application/x-java-serialized-object");
			
			// data for sending
			String input = "nodes";

			// send data to the servlet
			OutputStream outstream = con.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(input);
			oos.flush();
			oos.close();

			// receive result from servlet
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));

			String inputLine;
			String result = "";
			while ((inputLine = in.readLine()) != null)
				result = result + inputLine + "\n";

			// process result
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(result));

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(is);

			// get the root element
			Element docEle = dom.getDocumentElement();

			// get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("node");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {

					// get the node element
					Element el = (Element) nl.item(i);

					// get the node id
					int nodeId = Integer.parseInt(el.getElementsByTagName("id")
							.item(0).getFirstChild().getNodeValue());

					// get node count
					int nodeCount = Integer.parseInt(el
							.getElementsByTagName("count").item(0)
							.getFirstChild().getNodeValue());
					
					// get status
					int nodeStat = Integer.parseInt(el
							.getElementsByTagName("status").item(0)
							.getFirstChild().getNodeValue());
					
					// get type
					int nodeType = Integer.parseInt(el
							.getElementsByTagName("type").item(0)
							.getFirstChild().getNodeValue());

					// get battery
					double nodeBattery = Double.parseDouble(el
							.getElementsByTagName("battery").item(0)
							.getFirstChild().getNodeValue());
					
					// get last contact
					Timestamp nodeLastContact = Timestamp.valueOf(el
							.getElementsByTagName("last_contact").item(0)
							.getFirstChild().getNodeValue());

					nodeList.add(new Node(nodeId, nodeCount, nodeStat, nodeType, nodeBattery, nodeLastContact));
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		Node[] nodes = new Node[nodeList.size()];
		return nodeList.toArray(nodes);
	}
	
	public static OccupancyStat[] getOccupancyStats(Timestamp time){
		
		ArrayList<OccupancyStat> statList = new ArrayList<OccupancyStat>();
        
		try {
			// setup connection
			URLConnection con = urlServlet.openConnection();

			// set properties
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type",
					"application/x-java-serialized-object");
			
			// data for sending
			String input = "occupancy";

			// send data to the servlet
			OutputStream outstream = con.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(input);
			oos.writeObject(time);
			oos.flush();
			oos.close();

			// receive result from servlet
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));

			String inputLine;
			String result = "";
			while ((inputLine = in.readLine()) != null)
				result = result + inputLine + "\n";

			// process result
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(result));

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(is);

			// get the root element
			Element docEle = dom.getDocumentElement();

			// get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("event");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {

					// get the event element
					Element el = (Element) nl.item(i);

					// get timestamp
					Timestamp eventTimestamp = Timestamp.valueOf(el
							.getElementsByTagName("timestamp").item(0)
							.getFirstChild().getNodeValue());
					
					// get number of vehicles
					int eventVehicles = Integer.parseInt(el.getElementsByTagName("vehicles")
							.item(0).getFirstChild().getNodeValue());
					
					// get number of vehicles of type0
					int eventType0 = Integer.parseInt(el.getElementsByTagName("type0")
							.item(0).getFirstChild().getNodeValue());
					
					// get number of vehicles of type1
					int eventType1 = Integer.parseInt(el.getElementsByTagName("type1")
							.item(0).getFirstChild().getNodeValue());
					
					// get number of vehicles of type2
					int eventType2 = Integer.parseInt(el.getElementsByTagName("type2")
							.item(0).getFirstChild().getNodeValue());
					
					// get number of vehicles of type3
					int eventType3 = Integer.parseInt(el.getElementsByTagName("type3")
							.item(0).getFirstChild().getNodeValue());
					
					// get number of vehicles of type4
					int eventType4 = Integer.parseInt(el.getElementsByTagName("type4")
							.item(0).getFirstChild().getNodeValue());

					statList.add(new OccupancyStat(eventTimestamp, eventVehicles, eventType0, eventType1, eventType2, eventType3, eventType4));
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		OccupancyStat[] stats = new OccupancyStat[statList.size()];
		return statList.toArray(stats);
	}

	public static Duration[] getStayTimeStats(){
		
		ArrayList<Duration> durationList = new ArrayList<Duration>();
        
		try {
			// setup connection
			URLConnection con = urlServlet.openConnection();

			// set properties
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type",
					"application/x-java-serialized-object");
			
			// data for sending
			String input = "stay_time";

			// send data to the servlet
			OutputStream outstream = con.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(input);
			oos.flush();
			oos.close();

			// receive result from servlet
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));

			String inputLine;
			String result = "";
			while ((inputLine = in.readLine()) != null)
				result = result + inputLine + "\n";

			// process result
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(result));

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(is);

			// get the root element
			Element docEle = dom.getDocumentElement();

			// get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("event");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {

					// get the event element
					Element el = (Element) nl.item(i);

					// get timestamp
					Duration eventDuration = Duration.valueOf(el
							.getElementsByTagName("duration").item(0)
							.getFirstChild().getNodeValue());

					durationList.add(eventDuration);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		Duration[] durations = new Duration[durationList.size()];
		return durationList.toArray(durations);
	}
	
	public static int[] getTypeStats(){
		
		int[] typeList = new int[Constants.TYPE.length];
		for (int i=0; i<Constants.TYPE.length; i++)
			typeList[i] = 0;
        
		try {
			// setup connection
			URLConnection con = urlServlet.openConnection();

			// set properties
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type",
					"application/x-java-serialized-object");
			
			// data for sending
			String input = "types";

			// send data to the servlet
			OutputStream outstream = con.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(input);
			oos.flush();
			oos.close();

			// receive result from servlet
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));

			String inputLine;
			String result = "";
			while ((inputLine = in.readLine()) != null)
				result = result + inputLine + "\n";

			// process result
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(result));

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(is);

			// get the root element
			Element docEle = dom.getDocumentElement();

			// get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("type");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {

					// get the type element
					Element el = (Element) nl.item(i);

					// get id
					int typeId = Integer.parseInt(el
							.getElementsByTagName("id").item(0)
							.getFirstChild().getNodeValue());
					
					// get amount
					int typeAmount = Integer.parseInt(el
							.getElementsByTagName("amount").item(0)
							.getFirstChild().getNodeValue());

					typeList[typeId] += typeAmount;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return typeList;
	}

	public static EntranceStat[] getEntranceStats() {

		ArrayList<EntranceStat> entranceList = new ArrayList<EntranceStat>();

		try {
			// setup connection
			URLConnection con = urlServlet.openConnection();

			// set properties
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type",
					"application/x-java-serialized-object");

			// data for sending
			String input = "entrance";

			// send data to the servlet
			OutputStream outstream = con.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(input);
			oos.flush();
			oos.close();

			// receive result from servlet
			BufferedReader in = new BufferedReader(new InputStreamReader(con
					.getInputStream()));

			String inputLine;
			String result = "";
			while ((inputLine = in.readLine()) != null)
				result = result + inputLine + "\n";

			// process result
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(result));

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(is);

			// get the root element
			Element docEle = dom.getDocumentElement();

			// get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("event");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {

					// get the entrance element
					Element el = (Element) nl.item(i);

					// get timestamp
					Timestamp entranceTimestamp = Timestamp.valueOf(el
							.getElementsByTagName("timestamp").item(0)
							.getFirstChild().getNodeValue());

					// get type
					int entranceType = Integer.parseInt(el.getElementsByTagName(
							"type").item(0).getFirstChild().getNodeValue());
					
					// get speed
					int entranceSpeed = Integer.parseInt(el.getElementsByTagName(
							"speed").item(0).getFirstChild().getNodeValue());
					
					// get direction
					int entranceDirection = Integer.parseInt(el.getElementsByTagName(
							"direction").item(0).getFirstChild().getNodeValue());

					entranceList.add(new EntranceStat(entranceTimestamp, entranceType, entranceSpeed, entranceDirection));
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		EntranceStat[] entrances = new EntranceStat[entranceList.size()];
		return entranceList.toArray(entrances);
	}

	public static String getOnlineSince(){
		
		Timestamp onlineSince = new Timestamp(0);
		
		try {
			// setup connection
			URLConnection con = urlServlet.openConnection();

			// set properties
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type",
					"application/x-java-serialized-object");
			
			// data for sending
			String input = "online_since";

			// send data to the servlet
			OutputStream outstream = con.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(input);
			oos.flush();
			oos.close();

			// receive result from servlet
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));

			String inputLine;
			String result = "";
			while ((inputLine = in.readLine()) != null)
				result = result + inputLine + "\n";

			// process result
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(result));

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(is);

			// get the root element
			Element docEle = dom.getDocumentElement();

			// get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("online_since");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {

					// get the event element
					Element el = (Element) nl.item(i);

					// get timestamp
					onlineSince = Timestamp.valueOf(el
							.getElementsByTagName("timestamp").item(0)
							.getFirstChild().getNodeValue());
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		Date contact = new Date(onlineSince.getTime());

		SimpleDateFormat out =
            new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		return out.format(contact).toString();
	}
	
	public static Timestamp getLatestEntry() {

		Timestamp latestEntry = new Timestamp(0);

		try {
			// setup connection
			URLConnection con = urlServlet.openConnection();

			// set properties
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type",
					"application/x-java-serialized-object");

			// data for sending
			String input = "latest_entry";

			// send data to the servlet
			OutputStream outstream = con.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outstream);
			oos.writeObject(input);
			oos.flush();
			oos.close();

			// receive result from servlet
			BufferedReader in = new BufferedReader(new InputStreamReader(con
					.getInputStream()));

			String inputLine;
			String result = "";
			while ((inputLine = in.readLine()) != null)
				result = result + inputLine + "\n";

			// process result
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(result));

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(is);

			// get the root element
			Element docEle = dom.getDocumentElement();

			// get a nodelist of elements
			NodeList nl = docEle.getElementsByTagName("latest_entry");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {

					// get the event element
					Element el = (Element) nl.item(i);

					// get timestamp
					latestEntry = Timestamp.valueOf(el
							.getElementsByTagName("timestamp").item(0)
							.getFirstChild().getNodeValue());
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return latestEntry;
	}
}
