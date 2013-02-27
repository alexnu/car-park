package carpark;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;


public class StayTimeChart extends JPanel {

    public StayTimeChart(int interval) {
        
        Duration[] durations = FeedParser.getStayTimeStats();
		
		// find longest value
		Duration longest = durations[0];
		for (int i=1; i<durations.length; i++){
			if (durations[i].isLongerThan(longest))
				longest = durations[i];
		}
		
		// make groups
		int numGroups = (int) Math.ceil(longest.getTotalSeconds()/(interval*60.0));
		int[] groups = new int[numGroups];
		
		for (int i=0; i<durations.length; i++){
			int mins = durations[i].getTotalSeconds()/60;
			int curGroup=0;
			
        	while( mins >= interval*(curGroup+1) )
        		curGroup++;
        	groups[curGroup]++;
        }
		
		// make percentages
		double perc[] = new double[numGroups];
		for (int i = 0; i < numGroups; i++){
			double value = groups[i]*100.0 / durations.length;
			double result = value * 100;
			result = Math.round(result);
			result = result / 100;
			perc[i] = result;
		}
		
		// make category names
		String[] categoryNames = new String[numGroups];
		for (int i = 0; i < numGroups; i++)
			categoryNames[i] = ""+interval*(i+1);
		categoryNames[numGroups-1] = ">"+interval*(numGroups);

		// add values to dataset
        final DefaultKeyedValues data = new DefaultKeyedValues();
        for (int i=0;i<numGroups;i++)
        	data.addValue(categoryNames[i], perc[i]);

        final CategoryDataset dataset = DatasetUtilities.createCategoryDataset("stay Time", data);

        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
            "Visit Time",  // chart title
            "Stay Time (minutes)",                     // domain axis label
            "Percentage (%)",                     // range axis label
            dataset,                        // data
            PlotOrientation.VERTICAL,
            false,                           // include legend
            true,
            false
        );
        
		// get a reference to the plot for further customization...
		final CategoryPlot plot = chart.getCategoryPlot();
		
		if (numGroups >= 13) {
			final CategoryAxis domainAxis = plot.getDomainAxis();
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		}
		
		plot.addAnnotation(new CategoryPointerAnnotation("longest stay: "+longest, categoryNames[numGroups-1], groups[numGroups-1]+2, 3.92));
		for (int i=0;i<numGroups;i++)
			plot.addAnnotation(new CategoryTextAnnotation(""+groups[i],categoryNames[i],perc[i]+1));

        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 350));
        add(chartPanel);
    }
}