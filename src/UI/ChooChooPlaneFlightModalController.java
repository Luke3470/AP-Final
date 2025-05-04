package UI;

public class ChooChooPlaneFlightModalController {

    ChooChooPlaneFlightModalController(String db_url,int date,int flightNum){
        ChooChooPlaneFlightModalModel model = new ChooChooPlaneFlightModalModel(db_url);
        ChooChooPlaneFlightModalView modalView = new ChooChooPlaneFlightModalView(model.getData(date,flightNum));

    }
}