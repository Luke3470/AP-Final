package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.PublicKey;

public class UI {


    final  boolean shouldFill = true;
    final  boolean shouldWeightX = true;
    final  boolean RIGHT_TO_LEFT = false;

    private int pagination;
    private int page;

    UI(){
        setPagination(25);
        setPage(1);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public void addSearchToPane(Container pane){

    }

    public void addCreateGraphToPane(Container pane){

        GridBagConstraints c = new GridBagConstraints(); // <<< NEW individual constraints!
        c.gridy = 0;
        c.weighty = 0;
        c.weightx = 0.5;
        c.insets = new Insets(0, 5, 0, 5); // optional padding around buttons
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;

        // First Button
        c.gridx = 4;
        JButton airportButton = new JButton("Airport Punctuality Graph");
        pane.add(airportButton, c);

        // Second Button
        c.gridx = 5;
        JButton airlineButton = new JButton("Airline Punctuality Graph");
        pane.add(airlineButton, c);

        // Third Button
        c.gridx = 6;
        JButton timeButton = new JButton("Time Period Punctuality Graph");
        pane.add(timeButton, c);

        c.gridx = 7;
        c.weightx = 0.1;
        JLabel loading = new JLabel("Loading...",SwingConstants.CENTER);
        pane.add(loading,c);
    }


    public void addFileSearchToPane(Container pane, GridBagConstraints c) {

        JButton dbButton = new JButton("Select DB Location");
        if (shouldWeightX) {
            c.weightx = 0.3;
        }
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.anchor = GridBagConstraints.PAGE_START;
        dbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser
                JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

                // Show open dialog; this method does not return until the dialog is closed
                int result = fileChooser.showOpenDialog(pane);

                // If a file is selected
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    dbButton.setText(selectedFile.getName());
                }
            }
        });
        pane.add(dbButton, c);

    }

    public void addSearchTableToPane(Container pane,GridBagConstraints c){

    }


    public void addJTableToPane(Container pane, GridBagConstraints c) {


        // Create table model and table
        String[] columnNames = {"Flight Number", "Date", "Arrival Airport","Departure Airport","Airline","Airline Code",
                "Delay","Delay Reason"
        };
        Object[][] data = {};

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        JTable table = new JTable(model);
        table.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(table);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 4;
        c.gridwidth = 4;
        c.gridheight = 5;
        c.gridy = 2;
        pane.add(scrollPane, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridy = 7;
        c.insets = new Insets(0, 5, 0, 5);
        JButton back = new JButton("Back");
        pane.add(back,c);

        c.gridx=5;
        JLabel page = new JLabel("Current Page "+getPage(),SwingConstants.CENTER);
        pane.add(page,c);

        c.gridx=6;
        JLabel pagination =new JLabel("Results per Page "+getPagination(),SwingConstants.CENTER);
        pane.add(pagination,c);

        c.gridx=7;
        JButton next = new JButton("Next");
        pane.add(next,c);
    }


    private  void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Choo Choo Plane");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        Container pane = frame.getContentPane();
        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }

        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        if (shouldFill) {
            //natural height, maximum width
            c.fill = GridBagConstraints.HORIZONTAL;
        }

        addFileSearchToPane(pane, c);
        addJTableToPane(pane, c);
        addCreateGraphToPane(pane);

        //Display the window.
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    public int getPagination() {
        return pagination;
    }

    public void setPagination(int pagination) {
        this.pagination = pagination;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
