package UI.Modal;

public class ChooChooPlaneFlightModalController {

    private ChooChooPlaneFlightModalView modalView;
    private ChooChooPlaneFlightModalModel modalModel;
    /**
     * Creates a Controller for a Modal which contains all data about a Specific Flight
     * The db_url argument Must give a sqllite Database connection string
     * <p>
     * returns immediately, As for this to be Instantiated there must be a valid flight_number and date.
     * Which is all the detail that is required in order to create the modal and Allow for the model to collect data
     * @param  dbURL  DB connection string usually passed from the main Flight Model
     * @param  date The date of record which was clicked on in the table
     * @param flightNum  The flight_number of record which was clicked on in the table
     * @see ChooChooPlaneFlightModalModel
     * @see ChooChooPlaneFlightModalView
     */
    public ChooChooPlaneFlightModalController(String dbURL,int date,int flightNum){
        modalModel = new ChooChooPlaneFlightModalModel(dbURL);
        modalView = new ChooChooPlaneFlightModalView(modalModel.getData(date,flightNum));
        modalView.getCloseButton().addActionListener(e->addClose());
    }

    /**
     * Add Closing of frame to the close button within the modal
     *
     */
    public void addClose(){
        modalView.getFrame().dispose();

    }
}