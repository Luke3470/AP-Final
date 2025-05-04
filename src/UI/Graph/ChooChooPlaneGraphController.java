package UI.Graph;

import UI.main.ChooChooPlaneView;
public class ChooChooPlaneGraphController {

    public ChooChooPlaneGraphController(String dbURL, String[][] filters, String graphTitle, String xLabel, String yLabel, ChooChooPlaneView error){
        ChooChooPlaneGraphModel model = new ChooChooPlaneGraphModel(dbURL);
        ChooChooPlaneGraphView view = new ChooChooPlaneGraphView(graphTitle,xLabel,yLabel,model.getGraphData(filters),error);
    }
}
