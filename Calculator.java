package Calculator_Demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Calculator extends JFrame implements ActionListener {
    private JTextField display;
    private List<String> history;
    private JTextArea historyDisplay;

    public Calculator() {
        setTitle("Calculator");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load and resize icons
        ImageIcon historyIcon = new ImageIcon(getClass().getResource("/Calculator_Demo/history.png"));
        Image historyImage = historyIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        historyIcon = new ImageIcon(historyImage);

        // Display field
        display = new JTextField();
        display.setFont(new Font("Arial", Font.PLAIN, 24));
        display.setEditable(false); // Make display non-editable

        // History button
        JButton historyButton = new JButton(historyIcon);
        historyButton.setPreferredSize(new Dimension(30, 30));
        historyButton.addActionListener(e -> showHistory());

        // Panel to hold display and history button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(display, BorderLayout.CENTER);
        topPanel.add(historyButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "00", "+",
                "C", "AC", "="
        };

        int[] gridX = {0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2};
        int[] gridY = {1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5};

        for (int i = 0; i < buttons.length; i++) {
            JButton button = new JButton(buttons[i]);
            button.setFont(new Font("Arial", Font.PLAIN, 24));
            button.addActionListener(this);
            gbc.gridx = gridX[i];
            gbc.gridy = gridY[i];
            buttonPanel.add(button, gbc);
        }

        add(buttonPanel, BorderLayout.CENTER);

        // History
        history = new ArrayList<>();
        historyDisplay = new JTextArea();
        historyDisplay.setFont(new Font("Arial", Font.PLAIN, 16));
        historyDisplay.setEditable(false);
        historyDisplay.setLineWrap(true); // Wrap text
        historyDisplay.setWrapStyleWord(true);
        historyDisplay.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border

        JScrollPane historyScroll = new JScrollPane(historyDisplay);
        historyScroll.setPreferredSize(new Dimension(300, 300)); // Adjust size as needed

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "=":
                try {
                    double result = evaluateExpression(display.getText());
                    String resultStr = formatResult(result);
                    history.add(display.getText() + " = " + resultStr);
                    display.setText(resultStr);
                } catch (Exception ex) {
                    display.setText("Error");
                    ex.printStackTrace();
                }
                break;
            case "C":
                display.setText("");
                break;
            case "AC":
                display.setText("");
                history.clear();
                historyDisplay.setText("");
                break;
            default:
                display.setText(display.getText() + command);
        }
    }

    private double evaluateExpression(String expression) throws Exception {
        // Simple evaluation logic for basic arithmetic expressions
        List<Double> numbers = new ArrayList<>();
        List<Character> operations = new ArrayList<>();
        StringBuilder currentNumber = new StringBuilder();

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                currentNumber.append(c);
            } else {
                numbers.add(Double.parseDouble(currentNumber.toString()));
                currentNumber.setLength(0); // clear the current number
                operations.add(c);
            }
        }
        numbers.add(Double.parseDouble(currentNumber.toString()));

        // Process multiplication and division first
        for (int i = 0; i < operations.size(); i++) {
            if (operations.get(i) == '*' || operations.get(i) == '/') {
                double num1 = numbers.remove(i);
                double num2 = numbers.remove(i);
                char op = operations.remove(i);
                double result = (op == '*') ? num1 * num2 : num1 / num2;
                numbers.add(i, result);
                i--; // Stay on the same index as the lists are updated
            }
        }

        // Process addition and subtraction
        double result = numbers.get(0);
        for (int i = 0; i < operations.size(); i++) {
            char op = operations.get(i);
            double num = numbers.get(i + 1);
            if (op == '+') {
                result += num;
            } else if (op == '-') {
                result -= num;
            }
        }

        return result;
    }

    private String formatResult(double result) {
        if (result == (int) result) {
            return String.valueOf((int) result);
        } else {
            return String.valueOf(result);
        }
    }

    private void showHistory() {
        historyDisplay.setText(String.join("\n", history)); // Update history display
        JOptionPane.showMessageDialog(this, new JScrollPane(historyDisplay), "History", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculator::new);
    }
}
