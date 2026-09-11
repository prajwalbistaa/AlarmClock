import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGui);
    }

    private static void createAndShowGui() {
        JFrame frame = new JFrame("Alarm Clock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 8));
        panel.add(new JLabel("Set Time :"));

        JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        daysPanel.add(new JLabel("Days:"));
        ButtonGroup daysGroup = new ButtonGroup();
        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        for (String day : days) {
            JRadioButton dayButton = new JRadioButton(day);
            daysGroup.add(dayButton);
            daysPanel.add(dayButton);
        }
        panel.add(daysPanel);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        timePanel.add(new JLabel("Time:"));
        timePanel.add(new JComboBox<>(createNumberOptions(1, 12)));
        timePanel.add(new JLabel(":"));
        timePanel.add(new JComboBox<>(createNumberOptions(0, 59)));
        timePanel.add(new JComboBox<>(new String[] { "AM", "PM" }));
        panel.add(timePanel);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String[] createNumberOptions(int start, int end) {
        String[] options = new String[end - start + 1];
        for (int index = 0; index < options.length; index++) {
            options[index] = String.format("%02d", start + index);
        }
        return options;

    }
}