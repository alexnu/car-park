package carpark;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;


public class TypeChart extends JPanel {

    public TypeChart() {
        
        int[] types = FeedParser.getTypeStats();

        final DefaultKeyedValues data = new DefaultKeyedValues();
        for (int i=0;i<types.length;i++)
        	data.addValue(Constants.TYPE[i]+"s", types[i]);

        //data.sortByValues(SortOrder.DESCENDING);
        final CategoryDataset dataset = DatasetUtilities.createCategoryDataset("Stay Time", data);

        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
            "Types of Vehicles",  // chart title
            "Type",                     // domain axis label
            "Number of Vehicles",                     // range axis label
            dataset,                        // data
            PlotOrientation.VERTICAL,
            false,                           // include legend
            true,
            false
        );

        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 350));
        add(chartPanel);
    }
}