package UI.Modal;

import javax.swing.*;
import java.awt.*;

public class ChooChooPlaneFlightModalView {

    public ChooChooPlaneFlightModalView(String[] data) {
        createGUI(data);
    }

    public void createGUI(String[] data) {
        String[] labels = {
                "Flight Number", "Date", "Departure Airport Name", "Departure Airport IATA",
                "Scheduled Departure", "Actual Departure", "Arrival Airport Name", "Arrival Airport IATA",
                "Scheduled Arrival", "Actual Arrival", "Airline Name", "Airline IATA Code",
                "Delay Reason", "Delay Length"
        };

        JFrame frame = new JFrame("Choo Choo Flight");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // So it doesn't exit the whole app
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
            textField.setEditable(false); // Optional: make it read-only
            pane.add(textField, c);
        }

        // Add Close Button
        c.gridx = 1;
        c.gridy = labels.length + 1;
        JButton closeButton = new JButton("Close");
        pane.add(closeButton, c);

        closeButton.addActionListener(e -> frame.dispose());

        frame.pack();
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }
}