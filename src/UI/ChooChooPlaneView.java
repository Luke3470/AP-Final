package UI;

import UI.Utils.PlaceholderTextField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private JScrollPane scrollPane;
    private List<JComponent> searchFields;
    private JLabel loading;
    private JDialog errorMessage;
    private JLabel error;
    private boolean hasPaginationRun;
    private final boolean shouldFill = true;
    private final boolean shouldWeightX = true;
    private final boolean RIGHT_TO_LEFT = false;
    private final String[] columnNames = {
            "Flight Number", "Date", "Arrival Airport", "Departure Airport", "Airline",
            "Airline Code", "Delay", "Delay Reason"
    };

    private final String[][] placeholders = {
            {"XXX","[A-Z]{3}"},
            {"XXX","[A-Z]{3}"},
            {"9999","\\d{1,4}"},
            {"Fake Airline",".*"},
            {"XX","[a-zA-Z0-9]{2}"},
            {"YYYYMMDD","(2019|2020|2021|2022|2023)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])"},
            {"YYYYMMDD","(2019|2020|2021|2022|2023)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])"},
            {"1O","\\d{1,4}"},
            {"None",".*"}};

    ChooChooPlaneView () {
        createAndShowGUI();
        this.hasPaginationRun = false;
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
                {"Arrival Airport","Departure Airport"},
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

        String[] placeholders =  Arrays.stream(getPlaceholders())
                .map(x -> x[0])
                .toArray(String[]::new);

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
        JComboBox<String> delayReasonField = new JComboBox<>();
        searchFields.add(delayReasonField);
        pane.add(delayReasonField, c);

        c.gridx = 2;
        submitButton = new JButton("GO!");
        pane.add(submitButton, c);
    }

    private void addTablePane(Container pane, GridBagConstraints c) {

        tableModel = new DefaultTableModel(new Object[0][0], columnNames);
        table = new JTable(tableModel);
        table.setRowSorter(new TableRowSorter<>(tableModel));
        scrollPane = new JScrollPane(table);

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
        loading = new JLabel("NA", SwingConstants.CENTER);
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

    public List<JComponent> getSearchFields() { return searchFields; }

    public void setLoading(){
        loading.setText("Loading . . .");
    }
    public void setLoaded(){
        loading.setText("Complete!");
    }

    public void resetScroll(){
        scrollPane.getViewport().setViewPosition(new Point(0,0));
    }

    public void clearTableData(){
        tableModel.setRowCount(0);
    }

    public void setTableData(Object [][] data){
        DefaultTableModel model = tableModel;

        // Save filter and sort keys
        TableRowSorter<DefaultTableModel> oldSorter;
        oldSorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
        List<? extends RowSorter.SortKey> sortKeys = oldSorter != null ? oldSorter.getSortKeys() : null;
        RowFilter<DefaultTableModel, Integer> filter = oldSorter != null ? (RowFilter<DefaultTableModel, Integer>) oldSorter.getRowFilter() : null;

        data = removeNullRows(data);
        model.setDataVector(data, columnNames);

        // Recreate sorter
        TableRowSorter<DefaultTableModel> newSorter = new TableRowSorter<>(model);
        if (sortKeys != null) newSorter.setSortKeys(sortKeys);
        if (filter != null) newSorter.setRowFilter(filter);

        table.setRowSorter(newSorter);
    }

    public void clearTableSelection() {
        table.clearSelection();
    }
    public Object[][] removeNullRows(Object[][] input) {
        return Arrays.stream(input)
                .filter(row -> row != null && Arrays.stream(row).anyMatch(Objects::nonNull))
                .toArray(Object[][]::new);
    }

    public Object[] isSorted() {
        RowSorter<? extends TableModel> sorter = table.getRowSorter();

        if (sorter != null) {
            List<? extends RowSorter.SortKey> sortKeys = sorter.getSortKeys();

            if (!sortKeys.isEmpty()) {
                RowSorter.SortKey key = sortKeys.get(0);
                int columnIndex = key.getColumn();
                SortOrder order = key.getSortOrder();
                String columnName = table.getColumnName(columnIndex);

                return new Object[] { columnName, order };
            }
        }

        return null; // or return new Object[] { null, SortOrder.UNSORTED };
    }

    public int getTotalColumns(){
        return columnNames.length;
    }

    public void updatePageLabel(int page, int maxPage) {

        this.pageLabel.setText("Current Page "+ page +" ("+ maxPage+")");
    }

    public void setComboBox(String [] results){
        List<JComponent> temp = getSearchFields();
        JComboBox combo = ((JComboBox) temp.getLast());

        combo.removeAllItems();
        for (String result : results) {
            combo.addItem(result);
        }
        searchFields.set((searchFields.size()-1),combo);
    }

    public void showError(String errorText){
        if ((errorMessage !=null)&&(errorMessage.isVisible())){
            errorMessage.dispose();
            error.setText(errorText);
            errorMessage.setLocationRelativeTo(frame);
            errorMessage.setVisible(true);
        }else{
            errorMessage = new JDialog(frame,"Error",true);
            error = new JLabel(errorText);
            errorMessage.add(error);
            errorMessage.pack();
            errorMessage.setLocationRelativeTo(frame);
            errorMessage.setVisible(true);
        }

    }


    public String[][] getPlaceholders() {
        return placeholders;
    }

    public JTableHeader getTableHeader(){
        return table.getTableHeader();
    }

    public boolean getHasPaginationRun() {
        return hasPaginationRun;
    }

    public void setHasPaginationRun(boolean hasPaginationRun) {
        this.hasPaginationRun = hasPaginationRun;
    }
}
