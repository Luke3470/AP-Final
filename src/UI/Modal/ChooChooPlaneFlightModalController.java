package UI.Modal;

public class ChooChooPlaneFlightModalController {

    public ChooChooPlaneFlightModalController(String db_url,int date,int flightNum){
        ChooChooPlaneFlightModalModel model = new ChooChooPlaneFlightModalModel(db_url);
        ChooChooPlaneFlightModalView modalView = new ChooChooPlaneFlightModalView(model.getData(date,flightNum));

    }
}