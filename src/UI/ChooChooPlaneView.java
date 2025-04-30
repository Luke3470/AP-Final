package UI;

import UI.Utils.PlaceholderTextField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChooChooPlaneView {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton backButton;
    private JButton nextButton;
    private JLabel pageLabel;
    private JTextField paginationField;
    private JButton submitButton;
    private JButton airportGraphButton;
    private JButton airlineGraphButton;
    private JButton timeGraphButton;
    private JButton dbButton;
    private List<JTextField> searchFields;
    private final boolean shouldFill = true;
    private final boolean shouldWeightX = true;
    private final boolean RIGHT_TO_LEFT = false;

    ChooChooPlaneView () {
        createAndShowGUI();
    }
    private void createAndShowGUI() {
        frame = new JFrame("Choo Choo Plane");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        if (shouldFill) {
            c.fill = GridBagConstraints.HORIZONTAL;
        }

        addFileSearchToPane(pane, c);
        addTablePane(pane, c);
        addSearchFormToPane(pane, c);
        addGraphButtonsToPane(pane);

        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }


    private void addFileSearchToPane(Container pane, GridBagConstraints c) {
        dbButton = new JButton("Select DB Location");
        if (shouldWeightX) c.weightx = 0.3;

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.PAGE_START;
        pane.add(dbButton, c);
    }

    private void addSearchFormToPane(Container pane, GridBagConstraints c) {
        searchFields = new ArrayList<>();
        String[][] labels = {
                {"Departure Airport", "Arrival Airport"},
                {"Flight Number", "Airline"},
                {"Airline Code", "Start Date"},
                {"End Date", "Delay"},
                {"Delay Reason", "Submit!"}
        };

        int row = 1;
        for (String[] pair : labels) {
            for (int i = 0; i < pair.length; i++) {
                c.gridx = i * 2;
                c.gridy = row * 2;
                JLabel label = new JLabel(pair[i], SwingConstants.CENTER);
                pane.add(label, c);
            }
            row++;
        }

        String[] placeholders = {"FLR", "FLR", "1234", "Horizon Air", "QX", "YYYYMMDD", "YYYYMMDD", "10", "QX"};

        int fieldIndex = 0;
        for (int r = 0; r < 4; r++) {
            for (int col = 0; col < 2; col++) {
                c.gridx = col * 2;
                c.gridy = r * 2 + 3;
                JTextField field = new PlaceholderTextField(placeholders[fieldIndex++]);
                searchFields.add(field);
                pane.add(field, c);
            }
        }

        // Last row has only one text field and one button
        c.gridx = 0;
        c.gridy = row * 2 - 1;
        JTextField delayReasonField = new PlaceholderTextField(placeholders[fieldIndex]);
        searchFields.add(delayReasonField);
        pane.add(delayReasonField, c);

        c.gridx = 2;
        submitButton = new JButton("GO!");
        pane.add(submitButton, c);
    }

    private void addTablePane(Container pane, GridBagConstraints c) {
        String[] columnNames = {"Flight Number", "Date", "Arrival Airport", "Departure Airport", "Airline", "Airline Code", "Delay", "Delay Reason"};
        tableModel = new DefaultTableModel(new Object[0][0], columnNames);
        table = new JTable(tableModel);
        table.setRowSorter(new TableRowSorter<>(tableModel));
        JScrollPane scrollPane = new JScrollPane(table);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 4;
        c.gridwidth = 4;
        c.gridheight = 10;
        c.gridy = 2;
        pane.add(scrollPane, c);

        // Pagination controls
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridy = 12;
        c.insets = new Insets(0, 5, 0, 5);

        c.gridx = 4;
        backButton = new JButton("Back");
        pane.add(backButton, c);

        c.gridx = 5;
        pageLabel = new JLabel("Current Page 1 (10)", SwingConstants.CENTER);
        pane.add(pageLabel, c);

        c.gridx = 6;
        paginationField = new JTextField("Results per Page 25");
        pane.add(paginationField, c);

        c.gridx = 7;
        nextButton = new JButton("Next");
        pane.add(nextButton, c);
    }

    private void addGraphButtonsToPane(Container pane) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.weightx = 0.5;
        c.insets = new Insets(0, 5, 0, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 4;
        airportGraphButton = new JButton("Airport Punctuality Graph");
        pane.add(airportGraphButton, c);

        c.gridx = 5;
        airlineGraphButton = new JButton("Airline Punctuality Graph");
        pane.add(airlineGraphButton, c);

        c.gridx = 6;
        timeGraphButton = new JButton("Time Period Punctuality Graph");
        pane.add(timeGraphButton, c);

        c.gridx = 7;
        JLabel loading = new JLabel("Loading...", SwingConstants.CENTER);
        pane.add(loading, c);
    }

    // --- Public getters for controller access ---
    public JFrame getFrame() { return frame; }

    public JTable getTable() { return table; }

    public DefaultTableModel getTableModel() { return tableModel; }

    public JButton getSubmitButton() { return submitButton; }

    public JButton getAirportGraphButton() { return airportGraphButton; }

    public JButton getAirlineGraphButton() { return airlineGraphButton; }

    public JButton getTimeGraphButton() { return timeGraphButton; }

    public JButton getDbButton() { return dbButton; }

    public JButton getNextButton() { return nextButton; }

    public JButton getBackButton() { return backButton; }

    public JTextField getPaginationField() { return paginationField; }

    public JLabel getPageLabel() { return pageLabel; }

    public List<JTextField> getSearchFields() { return searchFields; }

    public void updatePageLabel(int page, int maxPage) {

        this.pageLabel.setText("Current Page "+ page +" ("+ maxPage+")");
    }
}
