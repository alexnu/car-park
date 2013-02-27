package carpark;

import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * A demo showing a time series with per minute data.
 *
 */
public class OccupancyChart extends JPanel {

    /**
     * A demonstration application.
     *
     * @param title  the frame title.
     */
    public OccupancyChart(Timestamp time) {
        
    	OccupancyStat[] stats = FeedParser.getOccupancyStats(time);
        Minute min;

        final TimeSeries series = new TimeSeries("total vehicles");
        for (int i=0; i<stats.length; i++){
        	min = new Minute(new Date(stats[i].getTimestamp().getTime()));
        	series.add(min, stats[i].getVehicles());
        }

        final TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Occupancy Chart",
            "Time", 
            "Occupied Spaces",
            dataset,
            true,
            true,
            false
        );
        
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 350));
        add(chartPanel);
    }
}