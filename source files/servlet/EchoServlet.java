import java.io.*;
import java.sql.*;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.*;

/**
 * Simple demonstration for an Applet <-> Servlet communication.
 */
public class EchoServlet extends HttpServlet {
	/**
	 * Get a String-object from the applet and send it back.
	 */
	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		try {
			response.setContentType("text/xml");

			// read a String-object from applet
			// instead of a String-object, you can transmit any object, which
			// is known to the servlet and to the applet
			InputStream in = request.getInputStream();
			ObjectInputStream inputFromApplet = new ObjectInputStream(in);
			String echo = (String) inputFromApplet.readObject();
			
			// connect to database
			String url = "jdbc:mysql://localhost/carpark";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = DriverManager.getConnection(url, "monitor", "monitor");
			
			// send an xml response to the applet
			PrintWriter out = response.getWriter();
			Statement stmt = conn.createStatement();
			
			if (echo.equals("nodes")) {
				
				// read node table
				ResultSet rs = stmt.executeQuery("SELECT * FROM node");

				out.println("<?xml version=\"1.0\"?>");
				out.println("<network>");

				while (rs.next()) {
					out.println("\t<node>");
					out.println("\t\t<id>"+rs.getInt("id")+"</id>");
					out.println("\t\t<count>"+rs.getInt("seq_no")+"</count>");
					out.println("\t\t<status>"+rs.getInt("status")+"</status>");
					out.println("\t\t<type>"+rs.getInt("type")+"</type>");
					out.println("\t\t<battery>"+rs.getFloat("battery")+"</battery>");
					out.println("\t\t<last_contact>"+rs.getTimestamp("last_contact")+"</last_contact>");
					out.println("\t</node>");
				}
				out.println("</network>");
				
			} else if (echo.equals("occupancy")){
				
				// read time parameter
				Timestamp param = (Timestamp) inputFromApplet.readObject();
				
				// read occupancy table
				String sqlStatement = "SELECT * FROM occupancy WHERE timestamp > ?";
				PreparedStatement pstmt = conn.prepareStatement(sqlStatement);
				pstmt.setTimestamp(1, param);
				ResultSet rs = pstmt.executeQuery();

				out.println("<?xml version=\"1.0\"?>");
				out.println("<network>");

				while (rs.next()) {
					out.println("\t<event>");
					out.println("\t\t<timestamp>"+rs.getTimestamp("timestamp")+"</timestamp>");
					out.println("\t\t<vehicles>"+rs.getInt("vehicles")+"</vehicles>");
					out.println("\t\t<type0>"+rs.getInt("type0")+"</type0>");
					out.println("\t\t<type1>"+rs.getInt("type1")+"</type1>");
					out.println("\t\t<type2>"+rs.getInt("type2")+"</type2>");
					out.println("\t\t<type3>"+rs.getInt("type3")+"</type3>");
					out.println("\t\t<type4>"+rs.getInt("type4")+"</type4>");
					out.println("\t</event>");
				}
				out.println("</network>");
				
			} else if (echo.equals("stay_time")){
				
				// read stay_time table
				ResultSet rs = stmt.executeQuery("SELECT * FROM stay_time");

				out.println("<?xml version=\"1.0\"?>");
				out.println("<network>");

				while (rs.next()) {
					out.println("\t<event>");
					out.println("\t\t<duration>"+rs.getTime("duration")+"</duration>");
					out.println("\t</event>");
				}
				out.println("</network>");
				
			} else if (echo.equals("types")){
				
				// read node table
				ResultSet rs = stmt.executeQuery("SELECT * FROM node WHERE status=1");
				
				ArrayList<Integer> idList = new ArrayList<Integer>();
				ArrayList<Integer> amountList = new ArrayList<Integer>();
				
				while (rs.next()) {
					int id = rs.getInt("type");
					if (!idList.contains(id)){
						idList.add(id);
						amountList.add(1);
					} else
						amountList.set(idList.indexOf(id), amountList.get(idList.indexOf(id)) + 1);
				}

				out.println("<?xml version=\"1.0\"?>");
				out.println("<types>");

				for (int i=0; i < idList.size(); i++) {
					out.println("\t<type>");
					out.println("\t\t<id>"+idList.get(i)+"</id>");
					out.println("\t\t<amount>"+amountList.get(i)+"</amount>");
					out.println("\t</type>");
				}
				out.println("</types>");
				
			} else if (echo.equals("entrance")){
				
				// read entrance table
				ResultSet rs = stmt.executeQuery("SELECT * FROM entrance ORDER BY timestamp DESC");

				out.println("<?xml version=\"1.0\"?>");
				out.println("<entrance>");

				while (rs.next()) {
					out.println("\t<event>");
					out.println("\t\t<timestamp>"+rs.getTimestamp("timestamp")+"</timestamp>");
					out.println("\t\t<type>"+rs.getInt("type")+"</type>");
					out.println("\t\t<speed>"+rs.getInt("speed")+"</speed>");
					out.println("\t\t<direction>"+rs.getInt("direction")+"</direction>");
					out.println("\t</event>");
				}
				out.println("</entrance>");
				
			} else if (echo.equals("online_since")){
				
				// read first timestamp
				ResultSet rs = stmt.executeQuery("SELECT * FROM event LIMIT 1");
				rs.next();

				out.println("<?xml version=\"1.0\"?>");
				out.println("<network>");
				out.println("\t<online_since>");
				out.println("\t\t<timestamp>"+rs.getTimestamp("timestamp")+"</timestamp>");
				out.println("\t</online_since>");
				out.println("</network>");
				
			} else if (echo.equals("latest_entry")){
				
				// read last timestamp
				ResultSet rs = stmt.executeQuery("SELECT * FROM occupancy ORDER BY timestamp DESC LIMIT 1");
				rs.next();

				out.println("<?xml version=\"1.0\"?>");
				out.println("<network>");
				out.println("\t<latest_entry>");
				out.println("\t\t<timestamp>"+rs.getTimestamp("timestamp")+"</timestamp>");
				out.println("\t</latest_entry>");
				out.println("</network>");
				
			} else {
				// string not recognized
			}
			
		    conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
