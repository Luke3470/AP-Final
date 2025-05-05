package UI.Graph;

import UI.main.ChooChooPlaneView;

import java.util.Map;

public class ChooChooPlaneGraphController {

    private ChooChooPlaneGraphView view;
    private ChooChooPlaneGraphModel model;


    public ChooChooPlaneGraphController(String dbURL,String graphTitle, String xLabel, String yLabel){
        model = new ChooChooPlaneGraphModel(dbURL);
        view = new ChooChooPlaneGraphView(graphTitle,xLabel,yLabel);
    }

    public void createBarChart(Map<Integer,String> mapParams,ChooChooPlaneView error,String groupBy){
        view.createBarChart(model.getBarChartData(mapParams,groupBy),error);

    }

    public void createLineChart(Map<Integer,String> mapParams,ChooChooPlaneView error){
        view.createLineChart(model.getLineChartData(mapParams),error);
    }
}

