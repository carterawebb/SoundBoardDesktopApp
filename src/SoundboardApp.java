import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;


public class SoundboardApp extends JFrame {

    private JList<String> soundList;
    private JButton playButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JButton addButton;
    private JButton deleteButton;
    private DefaultListModel<String> soundListModel;
    private Clip audioClip;
    private boolean isPlaying;

    public SoundboardApp() {
        setTitle("Soundboard App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        soundListModel = new DefaultListModel<>();
        soundList = new JList<>(soundListModel);

        playButton = new JButton("Play");
        pauseButton = new JButton("Pause");
        stopButton = new JButton("Stop");
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        add(new JScrollPane(soundList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playSelectedSound();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseSound();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSound();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addSound();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedSound();
            }
        });

        loadSounds(); // Load saved sounds on startup

        pack();
        setLocationRelativeTo(null);
    }


    private void playSelectedSound() {
        if (isPlaying) {
            stopSound();
        }

        int selectedIndex = soundList.getSelectedIndex();
        if (selectedIndex != -1) {
            String soundFilePath = soundListModel.getElementAt(selectedIndex);
            try {
                JFXPanel fxPanel = new JFXPanel(); // Initialize JavaFX platform

                Media sound = new Media(new File(soundFilePath).toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(sound);

                mediaPlayer.setOnEndOfMedia(new Runnable() {
                    @Override
                    public void run() {
                        isPlaying = false;
                        mediaPlayer.stop();
                    }
                });

                mediaPlayer.play();
                isPlaying = true;
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error playing sound", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void pauseSound() {
        if (audioClip != null && isPlaying) {
            audioClip.stop();
            isPlaying = false;
        }
    }

    private void stopSound() {
        if (audioClip != null) {
            audioClip.stop();
            audioClip.close();
            isPlaying = false;
        }
    }

    private void addSound() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Sound Files", "wav", "mp3"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String soundFilePath = selectedFile.getAbsolutePath();
            soundListModel.addElement(soundFilePath);
        }
    }

    private void deleteSelectedSound() {
        int selectedIndex = soundList.getSelectedIndex();
        if (selectedIndex != -1) {
            soundListModel.remove(selectedIndex);
        }
    }

    private void loadSounds() {
        try {
            File file = new File("sounds.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    soundListModel.addElement(line);
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSounds() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("sounds.txt"));
            for (int i = 0; i < soundListModel.size(); i++) {
                String soundFilePath = soundListModel.get(i);
                writer.write(soundFilePath);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        saveSounds(); // Save added sounds before closing
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SoundboardApp soundboardApp = new SoundboardApp();
                soundboardApp.setVisible(true);
            }
        });
    }
}
