package carpark;

import java.awt.Color;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * A demo showing a time series with per minute data.
 *
 */
public class OccupancyByTypeChart extends JPanel {

    /**
     * A demonstration application.
     *
     * @param title  the frame title.
     */
    public OccupancyByTypeChart(Timestamp time) {
        
    	OccupancyStat[] stats = FeedParser.getOccupancyStats(time);
        Minute min;

        final TimeSeries series0 = new TimeSeries(Constants.TYPE[0]+"s");
        for (int i=0; i<stats.length; i++){
        	min = new Minute(new Date(stats[i].getTimestamp().getTime()));
        	series0.add(min, stats[i].getType0());
        }
        final TimeSeries series1 = new TimeSeries(Constants.TYPE[1]+"s");
        for (int i=0; i<stats.length; i++){
        	min = new Minute(new Date(stats[i].getTimestamp().getTime()));
        	series1.add(min, stats[i].getType1());
        }
        final TimeSeries series2 = new TimeSeries(Constants.TYPE[2]+"s");
        for (int i=0; i<stats.length; i++){
        	min = new Minute(new Date(stats[i].getTimestamp().getTime()));
        	series2.add(min, stats[i].getType2());
        }
        final TimeSeries series3 = new TimeSeries(Constants.TYPE[3]+"s");
        for (int i=0; i<stats.length; i++){
        	min = new Minute(new Date(stats[i].getTimestamp().getTime()));
        	series3.add(min, stats[i].getType3());
        }
        final TimeSeries series4 = new TimeSeries(Constants.TYPE[4]+"s");
        for (int i=0; i<stats.length; i++){
        	min = new Minute(new Date(stats[i].getTimestamp().getTime()));
        	series4.add(min, stats[i].getType4());
        }

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series0);
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);
        dataset.addSeries(series4);
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Occupancy By Type",
            "Time", 
            "Occupied Spaces",
            dataset,
            true,
            true,
            false
        );
        
        final XYPlot plot = chart.getXYPlot();
        final XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.blue);
        renderer.setSeriesPaint(1, Color.red);
        renderer.setSeriesPaint(2, Color.green);
        renderer.setSeriesPaint(3, Color.cyan);
        renderer.setSeriesPaint(4, Color.gray);
        
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 350));
        add(chartPanel);
    }
}