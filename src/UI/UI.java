package UI;

import UI.Utils.PlaceholderTextField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;


public class UI {


    final  boolean shouldFill = true;
    final  boolean shouldWeightX = true;
    final  boolean RIGHT_TO_LEFT = false;

    private int pagination;
    private int page;

    private int maxpage;

    UI(){
        setPagination(25);
        setPage(1);
        setMaxPage(10);

        javax.swing.SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    public void addSearchToPane(Container pane,GridBagConstraints c){
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;

        JLabel depAir = new JLabel("Departure Airport",SwingConstants.CENTER);
        pane.add(depAir,c);

        c.gridx = 2;
        JLabel arrAir = new JLabel("Arrival Airport",SwingConstants.CENTER);
        pane.add(arrAir,c);

        c.gridx = 0;
        c.gridy = 2;
        JTextField depAirField = new PlaceholderTextField("FLR");
        pane.add(depAirField,c);

        c.gridx = 2;
        JTextField arrAirField = new PlaceholderTextField("FLR");
        pane.add(arrAirField,c);

        c.gridx = 0;
        c.gridy = 3;
        JLabel fliNum = new JLabel("Flight Number",SwingConstants.CENTER);
        pane.add(fliNum,c);

        c.gridx = 2;
        JLabel air = new JLabel("Airline",SwingConstants.CENTER);
        pane.add(air,c);

        c.gridx = 0;
        c.gridy = 4;
        JTextField fliNumField = new PlaceholderTextField("1234");
        pane.add(fliNumField,c);

        c.gridx = 2;
        JTextField airField = new PlaceholderTextField("Horizon Air");
        pane.add(airField,c);

        c.gridx = 0;
        c.gridy = 5;

        JLabel airCode = new JLabel("Airline Code",SwingConstants.CENTER);
        pane.add(airCode,c);

        c.gridx = 2;
        JLabel strDate = new JLabel("Start Date",SwingConstants.CENTER);
        pane.add(strDate,c);

        c.gridx = 0;
        c.gridy = 6;
        JTextField airCodeField = new PlaceholderTextField("QX");
        pane.add(airCodeField,c);

        c.gridx = 2;
        JTextField strDateField = new PlaceholderTextField("YYYYMMDD");
        pane.add(strDateField,c);

        c.gridx = 0;
        c.gridy = 7;

        JLabel endDate = new JLabel("End Data",SwingConstants.CENTER);
        pane.add(endDate,c);

        c.gridx = 2;
        JLabel delay = new JLabel("Delay",SwingConstants.CENTER);
        pane.add(delay,c);

        c.gridx = 0;
        c.gridy = 8;
        JTextField endDateField = new PlaceholderTextField("YYYYMMDD");
        pane.add(endDateField,c);

        c.gridx = 2;
        JTextField delayField = new PlaceholderTextField("10");
        pane.add(delayField,c);

        c.gridx = 0;
        c.gridy = 9;

        JLabel delReas = new JLabel("Delay Reason",SwingConstants.CENTER);
        pane.add(delReas,c);

        c.gridx = 2;
        JLabel submit = new JLabel("Submit!",SwingConstants.CENTER);
        pane.add(submit,c);

        c.gridx = 0;
        c.gridy = 10;
        JTextField delReasField = new PlaceholderTextField("QX");
        pane.add(delReasField,c);

        c.gridx = 2;
        JButton submitButt = new JButton("GO!");
        pane.add(submitButt,c);
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
        dbButton.addActionListener(e -> {
            // Create a file chooser
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));

            // Show open dialog; this method does not return until the dialog is closed
            int result = fileChooser.showOpenDialog(pane);

            // If a file is selected
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                dbButton.setText(selectedFile.getName());
            }
        });
        pane.add(dbButton, c);

    }

    private boolean checkAndUpdateValue(String value) {
        try {
            int num = Integer.parseInt(value); // Try parsing the value as an integer
            if (num < 301) {
                return true;
            } else {
                System.out.println("Input is not valid. It should be below 300.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid integer.");
            return false;
        }
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
        c.gridheight = 10;
        c.gridy = 2;
        pane.add(scrollPane, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridy = 12;
        c.insets = new Insets(0, 5, 0, 5);

        c.gridx=5;
        JLabel page = new JLabel("Current Page "+getPage() +" ("+ getMaxPage()+")",SwingConstants.CENTER);
        pane.add(page,c);

        c.gridx = 4;
        JButton back = new JButton("Back");
        pane.add(back,c);
        back.addActionListener(e -> {
            int val = getPage();
            if (val > 1){
                setPage(getPage()-1);
                page.setText("Current Page "+getPage() +" ("+ getMaxPage()+")");
            }
        });


        c.gridx=6;
        JTextField pagination = new JTextField("Results per Page "+getPagination());
        pagination.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                pagination.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Update the variable when the text field loses focus
                String editableValue = pagination.getText();
                if(checkAndUpdateValue(editableValue))
                    setPagination(Integer.parseInt(editableValue));
                pagination.setText("Results per Page "+ getPagination());

            }
        });

        pagination.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String editableValue = pagination.getText();
                    if(checkAndUpdateValue(editableValue))
                        setPagination(Integer.parseInt(editableValue));
                    pagination.setText("Results per Page "+ getPagination());
                    table.requestFocus();
                }
            }
        });


        pane.add(pagination,c);

        c.gridx=7;
        JButton next = new JButton("Next");
        next.addActionListener(e -> {
            int val = getPage();
            if (val < getMaxPage()){
                setPage(getPage()+1);
                page.setText("Current Page "+getPage() +" ("+ getMaxPage()+")");
            }
        });

        pane.add(next,c);
    }


    private  void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Choo Choo Plane");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        Container pane = frame.getContentPane();

        try{
            UIManager.createLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | InstantiationError e) {
            /* Do nothing and USe Normal Look and Feel */
        }


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
        addSearchToPane(pane,c);
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

    public int getMaxPage() {
        return maxpage;
    }

    public void setMaxPage(int maxpage) {
        this.maxpage = maxpage;
    }
}
