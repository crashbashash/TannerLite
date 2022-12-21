import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class TannerLiteGUI extends JFrame {
    private final JPanel mainPanel;
    private final JComboBox<Tannables> tannablesComboBox;
    private final JButton saveButton;
    private final JButton startButton;

    private TannerSettings settings = new TannerSettings();

    private final TannerLite bot;

    public TannerLiteGUI(TannerLite _bot) {
        bot = _bot;
        setTitle("Tanner Lite [ALPHA]");
        setSize(300, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);

        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout());
        tannablesComboBox = new JComboBox<>(Tannables.values());
        saveButton = new JButton("Save");
        startButton = new JButton("Start");


        mainPanel.add(tannablesComboBox);
        mainPanel.add(saveButton);
        mainPanel.add(startButton);

        saveButton.addActionListener(e -> saveSettings());

        startButton.addActionListener(e -> startBot());


        setContentPane(mainPanel);
        pack();
        setVisible(true);
    }

    private void saveSettings() {
        settings.setTannable((Tannables) tannablesComboBox.getSelectedItem());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("TannerLite.json"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(settings, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startBot() {
        bot.getSettings().setTannable((Tannables) tannablesComboBox.getSelectedItem());
        bot.startBot();
    }
}
