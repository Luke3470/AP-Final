package UI;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

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
    }

    private void onSubmit() {
        // Collect search values and perform actions
        var fields = view.getSearchFields();
        for (JTextField field : fields) {
            System.out.println("Search value: " + field.getText());
        }

        // You can add model interaction here later
    }

    private void onSelectDb() {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        int result = fileChooser.showOpenDialog(view.getFrame());

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            view.getDbButton().setText((selectedFile).getName());
        }
    }

    private void onBack() {
        int current = model.getPage();
        if (current > 1) {
            model.setPage(current - 1);
            view.updatePageLabel(model.getPage(), model.getMaxPage());
        }
    }

    private void onNext() {
        int current = model.getPage();
        if (current < model.getMaxPage()) {
            model.setPage(current + 1);
            view.updatePageLabel(model.getPage(), model.getMaxPage());
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
        String text = view.getPaginationField().getText();
        try {
            int val = Integer.parseInt(text);
            if (val < 301) {
                model.setPagination(val);
                view.getPaginationField().setText("Results per Page " + val);
            } else {
                System.out.println("Value must be less than 301.");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number.");
        }
    }
}
