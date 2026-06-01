package effects.audio.effectoperations.operations;

import effects.audio.effectoperations.EffectOperation;
import effects.audio.gui.menus.LoadingScreen;
import effects.audio.params.*;
import org.slf4j.*;

import javax.sound.sampled.*;
import java.io.*;

public class EchoOperation extends EffectOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger("Echo-Operation");

    private AudioFormat format;
    private byte[] contents;
    private byte[] modifiedContents;
    private int channels;
    private float sampleRate;
    private int bitDepth;
    private String encoding;
    long frames;

    public EchoOperation() {}

    @Override
    public void start(LoadingScreen screen, Parameters parameters) {
        file = screen.selectedFile;
        //lock(file);

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

        screen.setStatus("Loading the flux capacitor...", 5);


        switch(encoding) {
            case "PCM_SIGNED" -> {}
            case "PCM_UNSIGNED", "PCM_FLOAT", "ULAW", "ALAW" -> convertToPcmSigned();
            default -> throw new UnsupportedOperationException("Unsupported audio file encoding");
        }

        //bereken de verwachte lengte na de echo
        //expected time = original + repetitions * delay
        float frameRate = format.getFrameRate();
        double durationSeconds = frames / frameRate;
        int repetitions = parameters.get("repetitions").getInt();
        int delaySeconds = parameters.get("delay").getInt();   //we're gonna need this
        double addedDuration = repetitions * delaySeconds;
        double expectedDuration = durationSeconds + addedDuration;
        int numBytes = (int) (expectedDuration * format.getFrameRate() * format.getFrameSize());

        modifiedContents = new byte[numBytes];

        for(int i = 0; i < contents.length; i++) {
            modifiedContents[i] = 0;
        }

        for(int i = 0; i < contents.length; i++) {
            modifiedContents[i] += contents[i];
        }

        //unlock();
    }


    private void convertToPcmSigned() {
        ByteArrayInputStream bais = new ByteArrayInputStream(contents);
        AudioInputStream sourceStream = new AudioInputStream(bais, format,
                contents.length / format.getFrameSize());

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
}