package effects.audio.effectoperations.operations;

import effects.audio.Window;
import effects.audio.effectoperations.EffectOperation;
import effects.audio.gui.menus.*;
import org.slf4j.*;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.util.Arrays;

public final class EchoOperation extends EffectOperation {
    public EchoOperation(LoadingScreen screen) {
        super(screen, "Echo-Operation");
    }

    @Override
    public void start() {
        this.parameters = screen.selectedEffect.getParameters();
        File file = screen.selectedFile;

        prepareModification(file);

        float frameRate = format.getFrameRate();
        double durationSeconds = frames / frameRate;
        int repetitions = this.parameters.get("repetitions").getInt();
        float delaySeconds = this.parameters.get("delay").getFloat();
        float amplitude = this.parameters.get("exponentialDecay").getFloat();

        double addedDuration = repetitions * delaySeconds;
        double expectedDuration = durationSeconds + addedDuration;

        int bytesPerSample = bitDepth / 8;
        int expectedBytes = (int) (expectedDuration * frameRate * format.getFrameSize()) + 1;

        int totalSamples = expectedBytes / bytesPerSample;
        int[] modificationBuffer = new int[totalSamples];
        Arrays.fill(modificationBuffer, 0);

        int sourceSamples = contents.length / bytesPerSample;
        int byteShift = 0;

        modifiedContents = new byte[expectedBytes];
        Arrays.fill(modifiedContents, (byte) 0);

        try {
            for (int i = 0; i <= repetitions; i++) {
                byteShift = getByteShift(i);
                int sampleShift = byteShift / bytesPerSample;
                double exponentiatedAmplitude = Math.pow(amplitude, i);

                for (int j = 0; j < sourceSamples; j++) {
                    int sample = decodeSample(contents, j * bytesPerSample, bytesPerSample);
                    modificationBuffer[j + sampleShift] += (int) (sample * exponentiatedAmplitude);

                    int part = 90;
                    float percentage = part / (float) repetitions * i + (float) j / (float) contents.length * part / (float) repetitions;
                    screen.setStatus((int) percentage);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            LOGGER.error("An error occured: expectedBytes: {}, contents.length + byteshift: {}", expectedBytes, contents.length + byteShift, e);
            JOptionPane.showMessageDialog(screen, "An error occured trying to apply the operation. Try again?");
            Window.setWindow(new MainMenu());
            return;
        }

        finish(modificationBuffer);
    }

    private int getByteShift(int repetition) {
        double durationSeconds = (int) (repetition * parameters.get("delay").getFloat());
        return (int) (durationSeconds * sampleRate * channels * bitDepth/8);
    }
}