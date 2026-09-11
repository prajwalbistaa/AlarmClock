import javax.swing.JCheckBox;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGui);
    }

    private static void createAndShowGui() {
        JFrame frame = new JFrame("Alarm Clock");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 8));
        panel.add(new JLabel("Set Alarm Time :"));

        JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        daysPanel.add(new JLabel("Days:"));
        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        JCheckBox[] dayButtons = new JCheckBox[days.length];
        for (String day : days) {
            int dayIndex = daysPanel.getComponentCount() - 1;
            dayButtons[dayIndex] = new JCheckBox(day);
            daysPanel.add(dayButtons[dayIndex]);
        }
        panel.add(daysPanel);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        timePanel.add(new JLabel("Time:"));
        JComboBox<String> hourBox = new JComboBox<>(createNumberOptions(1, 12));
        hourBox.setSelectedIndex(-1);
        timePanel.add(hourBox);
        timePanel.add(new JLabel(":"));
        JComboBox<String> minuteBox = new JComboBox<>(createNumberOptions(0, 59));
        minuteBox.setSelectedIndex(-1);
        timePanel.add(minuteBox);
        JComboBox<String> periodBox = new JComboBox<>(new String[] { "AM", "PM" });
        periodBox.setSelectedIndex(-1);
        timePanel.add(periodBox);
        panel.add(timePanel);

        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(event -> setAlarm(
                frame, dayButtons, hourBox, minuteBox, periodBox));
        panel.add(doneButton);

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void setAlarm(JFrame frame, JCheckBox[] dayButtons,
            JComboBox<String> hourBox, JComboBox<String> minuteBox,
            JComboBox<String> periodBox) {
        if (!hasSelectedDay(dayButtons) || hourBox.getSelectedItem() == null
                || minuteBox.getSelectedItem() == null || periodBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(frame,
                    "Select at least one day and complete the alarm time.",
                    "Incomplete alarm details", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Set<DayOfWeek> selectedDays = EnumSet.noneOf(DayOfWeek.class);
        for (int index = 0; index < dayButtons.length; index++) {
            if (dayButtons[index].isSelected()) {
                selectedDays.add(DayOfWeek.of(index + 1));
            }
        }

        int hour = Integer.parseInt((String) hourBox.getSelectedItem());
        int minute = Integer.parseInt((String) minuteBox.getSelectedItem());
        if ("PM".equals(periodBox.getSelectedItem()) && hour != 12) {
            hour += 12;
        } else if ("AM".equals(periodBox.getSelectedItem()) && hour == 12) {
            hour = 0;
        }

        Alarm alarm = new Alarm(selectedDays, hour, minute);
        alarm.start(frame);
        JOptionPane.showMessageDialog(frame, "Alarm set successfully.",
                "Alarm Clock", JOptionPane.INFORMATION_MESSAGE);
    }

    private static boolean hasSelectedDay(JCheckBox[] dayButtons) {
        for (JCheckBox dayButton : dayButtons) {
            if (dayButton.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private static void showAlarmWindow(JFrame parent, Clip clip) {
        JFrame alarmFrame = new JFrame("Alarm");
        alarmFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        alarmFrame.setLayout(new BorderLayout(10, 10));
        alarmFrame.add(new JLabel("Alarm ringing!", JLabel.CENTER), BorderLayout.CENTER);

        JButton stopButton = new JButton("Stop Alarm");
        stopButton.addActionListener(event -> {
            clip.stop();
            clip.close();
            alarmFrame.dispose();
        });
        alarmFrame.add(stopButton, BorderLayout.SOUTH);
        alarmFrame.setSize(260, 130);
        alarmFrame.setLocationRelativeTo(parent);
        alarmFrame.setAlwaysOnTop(true);
        alarmFrame.setVisible(true);
    }

    private static class Alarm {
        private final Set<DayOfWeek> days;
        private final int hour;
        private final int minute;
        private LocalDateTime lastTriggered;

        private Alarm(Set<DayOfWeek> days, int hour, int minute) {
            this.days = days;
            this.hour = hour;
            this.minute = minute;
        }

        private void start(JFrame parent) {
            Timer timer = new Timer(1000, event -> check(parent));
            timer.setInitialDelay(0);
            timer.start();
        }

        private void check(JFrame parent) {
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            if (days.contains(now.getDayOfWeek()) && now.getHour() == hour
                    && now.getMinute() == minute && !now.equals(lastTriggered)) {
                lastTriggered = now;
                play(parent);
            }
        }

        private void play(JFrame parent) {
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(new File("AlarmMusic/music.wav")));
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                showAlarmWindow(parent, clip);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException exception) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(parent,
                        "Could not play AlarmMusic/music.wav.", "Alarm Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static String[] createNumberOptions(int start, int end) {
        String[] options = new String[end - start + 1];
        for (int index = 0; index < options.length; index++) {
            options[index] = String.format("%02d", start + index);
        }
        return options;

    }
}