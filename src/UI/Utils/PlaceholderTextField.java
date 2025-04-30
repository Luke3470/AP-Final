package UI.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class PlaceholderTextField extends JTextField {

    private String placeholder;
    private final Color placeholderColor = Color.GRAY;
    private final Color textColor = Color.BLACK;

    // Constructor to create a JTextField with a placeholder
    public PlaceholderTextField(String placeholder) {
        super(); // Calls the parent constructor (JTextField)
        this.placeholder = placeholder;
        setText(placeholder);
        setForeground(placeholderColor); // Placeholder text color

        // Add focus listener to handle placeholder behavior
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholder)) {
                    setText("");
                    setForeground(textColor);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    setText(placeholder);
                    setForeground(placeholderColor);
                }
            }
        });
    }
}
