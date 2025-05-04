package UI.Graph;

import UI.main.ChooChooPlaneView;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

public class ChooChooPlaneGraphView extends JFrame {

    private String graphTitle;
    private String xlabel;
    private String ylabel;
    private DefaultCategoryDataset graphData;

    public ChooChooPlaneGraphView(String graphTitle, String xlabel, String ylabel, Object[][] data, ChooChooPlaneView error) {
        this.graphTitle = graphTitle;
        this.xlabel = xlabel;
        this.ylabel = ylabel;
        this.graphData = new DefaultCategoryDataset();

        if (data != null) {

            addToDataSet(data);

            JFreeChart barChart = ChartFactory.createLineChart(
                    this.graphTitle,
                    this.xlabel,
                    this.ylabel,
                    this.graphData,
                    PlotOrientation.VERTICAL,
                    true,
                    false,
                    false
            );

            ChartPanel chartPanel = new ChartPanel(barChart);

            this.add(chartPanel);
            this.pack();
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
            this.setLocationRelativeTo(null);
            this.setVisible(true);
        }else {
            error.showError("No data found");
        }
    }

    private void addToDataSet(Object[][] data) {
        for (Object[] row : data) {
            Number value = (Number) row[0];
            Comparable rowKey = (Comparable) row[1];
            Comparable columnKey = (Comparable) row[2];

            graphData.addValue(value, rowKey, columnKey);
        }
    }
}