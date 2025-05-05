package UI.Graph;

import UI.main.ChooChooPlaneView;

import java.util.Map;

/**
 * Controller for Both line and Bar Graphs Could be made into an abstract class but as only two types of graph created
 * did not think it was necessary
 *
 * @author Luke Cadman
 */

public class ChooChooPlaneGraphController {

    private ChooChooPlaneGraphView view;
    private ChooChooPlaneGraphModel model;


    /**
     * Constructor to Create Both graph model and graph view
     *
     * @param dbURL sqllite DB connection String
     * @param graphTitle Title of Created Graph
     * @param xLabel Label of the X-Axis
     * @param yLabel Label of the Y-Axis
     */
    public ChooChooPlaneGraphController(String dbURL,String graphTitle, String xLabel, String yLabel){
        model = new ChooChooPlaneGraphModel(dbURL);
        view = new ChooChooPlaneGraphView(graphTitle,xLabel,yLabel);
    }

    /**
     * Creates a bar Chart JFrame
     *
     * @param mapParams Map of filters required
     * @param error Main JFrame to allow for error to be triggered if no results
     * @param groupBy Group by Either Airline or Airport
     */
    public void createBarChart(Map<Integer,String> mapParams,ChooChooPlaneView error,String groupBy){
        view.createBarChart(model.getBarChartData(mapParams,groupBy),error);

    }

    /**
     * Creates a Line Chart JFrame
     *
     * @param mapParams Map of filters required
     * @param error Main JFrame to allow for error to be triggered if no results
     */
    public void createLineChart(Map<Integer,String> mapParams,ChooChooPlaneView error){
        view.createLineChart(model.getLineChartData(mapParams),error);
    }
}

