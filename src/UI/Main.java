package UI;

import UI.main.ChooChooPlaneController;
import UI.main.ChooChooPlaneFlightModel;
import UI.main.ChooChooPlaneView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChooChooPlaneView view = new ChooChooPlaneView();
            ChooChooPlaneFlightModel model = new ChooChooPlaneFlightModel();
            ChooChooPlaneController controller = new ChooChooPlaneController(view, model);
        });
    }


}