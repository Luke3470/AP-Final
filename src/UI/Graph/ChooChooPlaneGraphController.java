package UI.Graph;

import UI.main.ChooChooPlaneView;
import org.sqlite.core.DB;

public class ChooChooPlaneGraphController {

    public ChooChooPlaneGraphController(String DBurl, String[][] filters, String graphTitle, String xLabel, String yLabel, ChooChooPlaneView error){
        ChooChooPlaneGraphModel model = new ChooChooPlaneGraphModel(DBurl);
        ChooChooPlaneGraphView view = new ChooChooPlaneGraphView(graphTitle,yLabel,yLabel,model.getGraphData(filters),error);
    }
}
