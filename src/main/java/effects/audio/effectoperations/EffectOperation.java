package effects.audio.effectoperations;

import effects.audio.gui.menus.LoadingScreen;

import javax.sound.sampled.*;
import java.io.*;

public abstract class EffectOperation {
    protected LoadingScreen screen;

    protected EffectOperation(LoadingScreen screen) {
        this.screen = screen;
    }

    protected abstract void start();

    protected static File convertToPcmSignedWav(File file) {
        try {
            AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(file);
            AudioFormat sourceFormat = mp3Stream.getFormat();

            // Step 2: Define the target PCM_SIGNED format
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    sourceFormat.getSampleRate(),
                    16,
                    sourceFormat.getChannels(),
                    sourceFormat.getChannels() * 2,
                    sourceFormat.getSampleRate(),
                    false
            );

            // Step 3: Convert MP3 -> PCM via mp3spi
            AudioInputStream pcmStream = AudioSystem.getAudioInputStream(targetFormat, mp3Stream);

            // Step 4: Write directly to a .wav file
            String filenameWithoutExtension = file.getName().substring(0, file.getName().lastIndexOf('.'));
            File tempFile = File.createTempFile(filenameWithoutExtension, ".wav");
            tempFile.deleteOnExit();
            AudioSystem.write(pcmStream, AudioFileFormat.Type.WAVE, tempFile);
            pcmStream.close();
            mp3Stream.close();

            return tempFile;
        } catch (IOException | UnsupportedAudioFileException e) {
            EffectOperationManager.LOGGER.error("Error converting mp3 to wav", e);
            throw new RuntimeException(e);
        }
    }
}
