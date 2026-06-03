package effects.audio.effectoperations.operations;

import effects.audio.Window;
import effects.audio.effectoperations.EffectOperation;
import effects.audio.gui.menus.*;
import effects.audio.params.Parameters;
import org.slf4j.*;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public final class EchoOperation extends EffectOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger("Echo-Operation");


    private Parameters parameters;
    private AudioFormat format;
    private byte[] contents;
    private byte[] modifiedContents;
    private int channels;
    private float sampleRate;
    private int bitDepth;
    private String encoding;
    long frames;

    public EchoOperation(LoadingScreen screen) {
        super(screen);
    }

    @Override
    public void start() {
        this.parameters = screen.selectedEffect.getParameters();
        File file = screen.selectedFile;
        String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);

        switch(extension) {
            case "wav" -> {}
            case "au", "aif", "mp3" -> file = convertToPcmSignedWav(file);
            default -> throw new UnsupportedOperationException("Unsupported audio file format: " + extension);
        }

        screen.setStatus("Deleting System32...", 0);
        try(AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            LOGGER.info("Parsing file {} to byte array", file.toPath());

            format = ais.getFormat();
            channels = format.getChannels();
            sampleRate = format.getSampleRate();
            frames = ais.getFrameLength();
            bitDepth = format.getSampleSizeInBits();
            encoding = format.getEncoding().toString();
            contents = ais.readAllBytes();
        } catch (IOException e) {
            LOGGER.error("Could not read file {}", file.toPath(), e);
            throw new UncheckedIOException(e);
        } catch(UnsupportedAudioFileException e) {
            LOGGER.error("Unsupported audio file format", e);
            throw new UnsupportedOperationException("Unsupported audio file format");
        }

        switch(extension) {
            case "wav" -> convertToPcmSigned();
            case "au", "aif", "mp3" -> {}
        }

        switch(encoding) {
            case "PCM_SIGNED" -> {}
            case "PCM_UNSIGNED", "PCM_FLOAT", "ULAW", "ALAW" -> convertToPcmSignedWav(file);
            default -> throw new UnsupportedOperationException("Unsupported audio file encoding: " + encoding);
        }

        float frameRate = format.getFrameRate();
        double durationSeconds = frames / frameRate;
        int repetitions = this.parameters.get("repetitions").getInt();
        float delaySeconds = this.parameters.get("delay").getFloat();
        float amplitude = this.parameters.get("exponentialDecay").getFloat();

        double addedDuration = repetitions * delaySeconds;
        double expectedDuration = durationSeconds + addedDuration;

        int bytesPerSample = bitDepth / 8;
        int expectedBytes = (int) (expectedDuration * format.getFrameRate() * format.getFrameSize()) + 1;

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
                    int part = 80;
                    float percentage = part / (float) repetitions * i + (float) j / (float) contents.length * part / (float) repetitions;
                    screen.setStatus("Loading the flux capacitor...", (int) percentage);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            LOGGER.error("An error occured: expectedBytes: {}, contents.length + byteshift: {}", expectedBytes, contents.length + byteShift, e);
            JOptionPane.showMessageDialog(screen, "An error occured trying to apply the operation. Try again?");
            Window.setWindow(new MainMenu());
            return;
        }

        modifiedContents = new byte[totalSamples * bytesPerSample];
        int maxSampleValue = (1 << (bitDepth - 1)) - 1;
        int minSampleValue = -(1 << (bitDepth - 1));
        for (int i = 0; i < totalSamples; i++) {
            int clamped = Math.clamp(modificationBuffer[i], minSampleValue, maxSampleValue);
            encodeSample(modifiedContents, i * bytesPerSample, bytesPerSample, clamped);
        }

        screen.setStatus("Loading the flux capacitor...", 90);

        File result = createModifiedFile();

        OperationExecutedScreen oescreen = new OperationExecutedScreen(this, result);
        Window.setWindow(oescreen);
    }

    private int getByteShift(int repetition) {
        double durationSeconds = (int) (repetition * parameters.get("delay").getFloat());
        return (int) (durationSeconds * sampleRate * channels * bitDepth/8);
    }

    private void convertToPcmSigned() {
        ByteArrayInputStream bais = new ByteArrayInputStream(contents);
        AudioInputStream sourceStream = new AudioInputStream(bais, format, contents.length / format.getFrameSize());

        AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                format.getSampleRate(),
                16,
                format.getChannels(),
                format.getChannels() * 2,
                format.getSampleRate(),
                false
        );

        if (!AudioSystem.isConversionSupported(targetFormat, format)) {
            throw new UnsupportedOperationException(
                    "Conversion not supported: " + format.getEncoding() + " -> PCM_SIGNED"
            );
        }

        AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream);

        try {
            contents = convertedStream.readAllBytes();
            format = convertedStream.getFormat();
            channels = format.getChannels();
            sampleRate = format.getSampleRate();
            bitDepth = format.getSampleSizeInBits();
            encoding = format.getEncoding().toString();
            LOGGER.info("Audio Format: {}", format.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private int decodeSample(byte[] data, int offset, int bytesPerSample) {
        int sample = 0;
        for (int b = 0; b < bytesPerSample; b++) {
            sample |= (data[offset + b] & 0xFF) << (8 * b);
        }
        // Sign-extend
        int bits = bytesPerSample * 8;
        if ((sample & (1 << (bits - 1))) != 0) {
            sample |= -(1 << bits);
        }
        return sample;
    }

    private void encodeSample(byte[] data, int offset, int bytesPerSample, int sample) {
        for (int b = 0; b < bytesPerSample; b++) {
            data[offset + b] = (byte) (sample >> (8 * b));
        }
    }

    private File createModifiedFile() {
        AudioFormat format = new AudioFormat(sampleRate, bitDepth, channels, true, false);
        try (AudioInputStream ais = new AudioInputStream(
                new ByteArrayInputStream(modifiedContents),
                format,
                modifiedContents.length / format.getFrameSize()
        )) {
            File temp = File.createTempFile("modified", ".wav");
            temp.deleteOnExit();
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, temp);
            return temp;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}