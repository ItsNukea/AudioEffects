package effects.audio.effectoperations.operations;

import effects.audio.effectoperations.EffectOperation;
import effects.audio.gui.menus.LoadingScreen;

import java.util.Arrays;

public final class EarrapeOperation extends EffectOperation {
    public EarrapeOperation(LoadingScreen screen) {
        super(screen, "Earrape-Operation");
    }

    @Override
    public void start() {
        file = screen.selectedFile;
        parameters = screen.selectedEffect.getParameters();

        prepareModification(file);

        float amplifier = parameters.get("amplifier").getFloat();
        int bytesPerSample = bitDepth / 8;
        int sourceSamples = contents.length / bytesPerSample;
        int[] modificationBuffer = new int[sourceSamples];
        Arrays.fill(modificationBuffer, 0);
        LOGGER.info("Bit depth: {}", bitDepth);

        for(int i = 0; i < sourceSamples; i++) {
            int sample = decodeSample(contents, i * bytesPerSample, bytesPerSample);
            double randomNumber = Math.random();
            int amplificationAmplifier = 1500;
            int delta = (int) (randomNumber * amplifier * amplificationAmplifier * bitDepth / 8);
            sample += delta;
            modificationBuffer[i] = sample;

            int part = 90;
            float percentage = (float) (i * part) / sourceSamples;
            screen.setStatus((int) percentage);
        }

        finish(modificationBuffer);
    }
}