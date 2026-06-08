package effects.audio.effectoperations;

import effects.audio.Window;
import effects.audio.gui.menus.*;
import effects.audio.params.Parameters;
import org.slf4j.*;

import javax.sound.sampled.*;
import java.io.*;

public abstract class EffectOperation {
    protected LoadingScreen screen;
    protected Logger LOGGER;
    protected File file;
    protected Parameters parameters;
    protected AudioFormat format;
    protected byte[] contents;
    protected byte[] modifiedContents;
    protected int channels;
    protected float sampleRate;
    protected int bitDepth;
    protected String encoding;
    protected long frames;

    protected EffectOperation(LoadingScreen screen, String loggerName) {
        this.screen = screen;
        this.LOGGER = LoggerFactory.getLogger(loggerName);
        this.file = screen.selectedFile;
    }

    protected abstract void start();

    protected void prepareModification(File file) {
        String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        switch(extension) {
            case "wav" -> {}
            case "au", "aif", "mp3" -> file = convertToPcmSignedWav(file);
            default -> throw new UnsupportedOperationException("Unsupported audio file format: " + extension);
        }

        screen.setStatus(0);
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

    protected File createModifiedFile() {
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

    protected int decodeSample(byte[] data, int offset, int bytesPerSample) {
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

    protected void encodeSample(byte[] data, int offset, int bytesPerSample, int sample) {
        for (int b = 0; b < bytesPerSample; b++) {
            data[offset + b] = (byte) (sample >> (8 * b));
        }
    }

    protected void finish(int[] modificationBuffer) {
        modifiedContents = new byte[modificationBuffer.length * bitDepth / 8];
        int maxSampleValue = (1 << (bitDepth - 1)) - 1;
        int minSampleValue = -(1 << (bitDepth - 1));
        for (int i = 0; i < modificationBuffer.length; i++) {
            int clamped = Math.clamp(modificationBuffer[i], minSampleValue, maxSampleValue);
            encodeSample(modifiedContents, i * bitDepth / 8, bitDepth / 8, clamped);
        }

        screen.setStatus(95);
        File resultFile = createModifiedFile();
        screen.setStatus(100);

        Window.setWindow(new OperationExecutedScreen(this, resultFile));
    }
}
