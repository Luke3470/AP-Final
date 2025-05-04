package UI.Graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

public class ChooChooPlaneGraphView extends JFrame {

    private String graphTitle;
    private String xlabel;
    private String ylabel;
    private DefaultCategoryDataset graphData;

    public ChooChooPlaneGraphView(String graphTitle, String xlabel, String ylabel, Object[][] data) {
        this.graphTitle = graphTitle;
        this.xlabel = xlabel;
        this.ylabel = ylabel;
        this.graphData = new DefaultCategoryDataset();

        addToDataSet(data);

        JFreeChart barChart = ChartFactory.createBarChart(
                this.graphTitle,
                this.xlabel,
                this.ylabel,
                this.graphData
        );

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);

        setTitle(this.graphTitle);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void addToDataSet(Object[][] data) {
        for (Object[] row : data) {
            if (row.length >= 3 && row[0] instanceof Number && row[1] instanceof String && row[2] instanceof String) {
                Number value = (Number) row[0];
                String rowKey = (String) row[1];
                String columnKey = (String) row[2];
                graphData.addValue(value, rowKey, columnKey);
            }
        }
    }
}