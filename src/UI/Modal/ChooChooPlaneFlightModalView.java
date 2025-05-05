package UI.Modal;

import javax.swing.*;
import java.awt.*;

public class ChooChooPlaneFlightModalView {
    private JButton closeButton;
    private JFrame frame;

    /**
     * Calls createGUI Which using a JFrame formats data provided by the data Model for the modal
     * a String array of data must be passed in the correct order to allow for formatting to match the assigned labels
     * <p>
     * method will return the GUI impediment when called as all correct information will be passed
     *
     * @param data String Array containing all data on a specific flight Ordered correctly
     * @see ChooChooPlaneFlightModalModel
     * */
    public ChooChooPlaneFlightModalView(String[] data) {
        createGUI(data);
    }

    /**
     * Creates and organised GUI for pop up model using GridBagConstraints
     * Method Creates Visible GUI
     * @param data String Array containing all data on a specific flight Ordered correctly
     * @see GridBagConstraints
     */
    public void createGUI(String[] data) {
        String[] labels = {
                "Flight Number", "Date", "Departure Airport Name", "Departure Airport IATA",
                "Scheduled Departure", "Actual Departure", "Arrival Airport Name", "Arrival Airport IATA",
                "Scheduled Arrival", "Actual Arrival", "Airline Name", "Airline IATA Code",
                "Delay Reason", "Delay Length"
        };

        frame = new JFrame("Choo Choo Flight");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container pane = frame.getContentPane();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 10, 5, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < labels.length; i++) {
            c.gridx = 0;
            c.gridy = i;

            JLabel label = new JLabel(labels[i]);
            pane.add(label, c);

            c.gridx = 1;
            JTextField textField = new JTextField(data[i]);
            textField.setEditable(false);
            pane.add(textField, c);
        }

        c.gridx = 1;
        c.gridy = labels.length + 1;
        closeButton = new JButton("Close");
        pane.add(closeButton, c);

        frame.pack();

        // Center on screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * This is a getter for the closeButton variable used so the controller can assign an action
     *
     * @return Button which closes the modal in order for controller to interact
     */

    public JButton getCloseButton() {
        return closeButton;
    }

    /**
     *
     * @return The frame of the modal to all for the controller to dispose() of it
     *
     * @see JFrame
     */
    public JFrame getFrame() {
        return frame;
    }
}