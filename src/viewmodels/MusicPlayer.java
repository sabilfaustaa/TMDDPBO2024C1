package viewmodels;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.InputStream;

public class MusicPlayer implements MusicControl {
    private Clip clip;

    @Override
    public void playBackgroundMusic(String filepath) {
        try {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            File file = new File(filepath);
            InputStream audioSrc = getClass().getClassLoader().getResourceAsStream(filepath);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(audioSrc);

            clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading music: " + e.getMessage());
        }
    }

    @Override
    public void pauseBackgroundMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    @Override
    public void stopBackgroundMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}
