package UI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.*;
import java.io.File;
import java.util.*;

public class ChooChooPlaneController {
    private final ChooChooPlaneView view;
    private final ChooChooPlaneFlightModel model;

    public ChooChooPlaneController(ChooChooPlaneView view, ChooChooPlaneFlightModel model) {
        this.view = view;
        this.model = model;
        // Register all listeners
        initController();
    }

    private void initController() {
        view.getSubmitButton().addActionListener(e -> onSubmit());
        view.getDbButton().addActionListener(e -> onSelectDb());
        view.getBackButton().addActionListener(e -> onBack());
        view.getNextButton().addActionListener(e -> onNext());
        view.getPaginationField().addFocusListener(new PaginationFocusHandler());
        view.getPaginationField().addKeyListener(new PaginationKeyHandler());
        view.getTableHeader().addMouseListener(new TableHeaderSortHandler());
        view.getTable().getSelectionModel().addListSelectionListener(this::onRowSelect);
    }

    private void onRowSelect(ListSelectionEvent e ){
        if (!e.getValueIsAdjusting()) {

            JTable table = view.getTable();
            int selectedRow = table.getSelectedRow();

            if (selectedRow < 0 || selectedRow >= table.getRowCount()) {
                return;
            }

            try {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                String tempFlightNumber = table.getModel().getValueAt(modelRow, 0).toString();
                String tempDate = table.getModel().getValueAt(modelRow, 1).toString();

                int flightNumber = Integer.parseInt(tempFlightNumber);
                int date = Integer.parseInt(tempDate);
                ChooChooPlaneFlightModalController flightModal =
                        new ChooChooPlaneFlightModalController(model.getDb_url(), date, flightNumber);
            } catch (Exception err) {
                System.err.println("Error processing row selection: " + err.getMessage());
                throw new RuntimeException(err);
            }
        }
    }


    private void onSubmit() {
        if (model.hasDB()) {
            view.setLoading();

            // Collect search values and perform actions
            String [][] placeholders = view.getPlaceholders();
            Map<Integer,String> mapSearchParams = new HashMap<>();
            var fields = view.getSearchFields();
            int count = 0;
            String val;

            for (JComponent field : fields) {
                if (field instanceof JTextField) {
                    val = ((JTextField) field).getText();
                }else{
                    val = (String) ((JComboBox<?>) field).getSelectedItem();
                }
                if (((val != null) && (!Objects.equals(placeholders[count][0], val)) && (val.matches(placeholders[count][1])))){
                    mapSearchParams.put(count,val);
                    System.out.println("Search value: " + val);
                }else {
                    System.out.println("No Change to Search Box: "+count);
                }
                count ++;
            }
            Object [] sort = view.isSorted();
            Object [][] results = model.getSearchResults(mapSearchParams,view.getTotalColumns(),sort);

            if (results[0][0] != null){
                view.updatePageLabel(model.getPage(), model.getMaxPage());
                view.setTableData(results);
                view.resetScroll();
                view.setLoaded();
            }else{
                view.showError("No Results for this query");
                view.clearTableData();
            }
        }else {
            view.showError("Please Select A Database");
        }
    }

    private void onSelectDb() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        int result = fileChooser.showOpenDialog(view.getFrame());

        if (result == JFileChooser.APPROVE_OPTION) {
            view.setLoading();
            File selectedFile = fileChooser.getSelectedFile();
            String extension = getFileExtension(selectedFile.getName());

            if (Objects.equals(extension, "sqlite")) {
                view.getDbButton().setText((selectedFile).getName());
                model.setDb_url(selectedFile);
                onSubmit();
                view.setLoaded();
                updateComboBox();
            }else {
                view.showError("This is not a SQL Lite DB");
            }
        }
    }
    private String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex > 0) {
            return fileName.substring(lastIndex + 1);
        }
        return null; // No extension
    }

    private void onBack() {
        int current = model.getPage();
        if (current > 1) {
            model.setPage(current - 1);
            view.updatePageLabel(model.getPage(), model.getMaxPage());
            onSubmit();
        }
    }

    private void updateComboBox(){
        if (model.hasDB()){
            view.setComboBox(model.getComboBoxResults());
        }
    }



    private void onNext() {
        int current = model.getPage();
        if (current < model.getMaxPage()) {
            model.setPage(current + 1);
            view.updatePageLabel(model.getPage(), model.getMaxPage());
            onSubmit();
        }
    }



    private class TableHeaderSortHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e){
            onSubmit();
        }

    }

    private class PaginationFocusHandler implements FocusListener {
        @Override
        public void focusGained(FocusEvent e) {
            view.getPaginationField().setText("");
        }

        @Override
        public void focusLost(FocusEvent e) {
            updatePaginationFromField();
            view.setHasPaginationRun(false);
        }
    }

    private class PaginationKeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                updatePaginationFromField();
                view.getTable().requestFocus();
            }
        }
    }

    private void updatePaginationFromField() {
        boolean hasRun = view.getHasPaginationRun();
        if (!hasRun){
            String text = view.getPaginationField().getText();
            try {
                view.setHasPaginationRun(true);
                int val = Integer.parseInt(text);
                if ((val < 301)&& (val > 24)) {
                    model.setPagination(val);
                    view.getPaginationField().setText("Results per Page "+val );
                    if(model.hasDB()) {
                        onSubmit();
                    }
                } else {
                    view.getPaginationField().setText("Results per Page 25");
                    System.out.println("Value must be less than 301 or Greater than 24");
                    view.showError("Value must be Above 24 or Bellow 301");
                }
            } catch (NumberFormatException ex) {
                view.getPaginationField().setText("Results per Page 25");
                System.out.println("Invalid number.");
                view.showError("Invalid Number");
            }
        }

    }
}
