package effects.audio.util;

import effects.audio.exceptions.ConversionException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

//INFO: The code in this class was copy-pasted from a library because I'm too lazy to import the entire library.
//Anyways, I did not write this code myself, but might have edited some lines, just so you know
public final class Converter {
	private final InputStream input;
	private AudioFormat audioFormat;
	private final boolean close;
	private final Type targetType;
	
	public Converter(InputStream input, Type targetType) {
		this(input, targetType, false);
	}
	
	public Converter(InputStream input, Type targetType, boolean close) {
		this.input = input;
		this.close = close;
		this.targetType = targetType;
	}
	
	public static Converter convertFrom(InputStream input, Type targetType) {
		return new Converter(input, targetType, false);
	}
	
	public static Converter convertFrom(byte[] mp3Content, Type targetType) {
		return new Converter(new ByteArrayInputStream(mp3Content), targetType, true);
	}
	
	public Converter withTargetFormat(AudioFormat targetAudioFormat) {
		this.audioFormat = targetAudioFormat;
		return this;
	}
	
	public void to(OutputStream output) {
		try (
				final ByteArrayOutputStream rawOutputStream = new ByteArrayOutputStream()
		) {
			convert(input, rawOutputStream, getTargetFormat());
			
			final byte[] rawResult = rawOutputStream.toByteArray();
			final AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(rawResult),
					getTargetFormat(), rawResult.length);
			AudioSystem.write(audioInputStream, targetType, output);
		} catch (Exception e) {
			throw new ConversionException(e);
		} finally {
			closeInput();
		}
	}
	
	public byte[] toByteArray() {
		try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			to(output);
			return output.toByteArray();
		}  catch (IOException e) {
			throw new ConversionException(e);
		}
	}
	
	private void closeInput() {
		if (this.close) {
			try {
				input.close();
			} catch (IOException e) {
				// Sad but true;
			}
		}
	}
	
	private void convert(InputStream input, OutputStream output, AudioFormat targetFormat) throws Exception {
		try (AudioInputStream rawSourceStream = AudioSystem.getAudioInputStream(input)) {
			AudioFormat sourceFormat = rawSourceStream.getFormat();
			AudioFormat convertFormat = getAudioFormat(sourceFormat);
			
			try (
					AudioInputStream sourceStream = AudioSystem.getAudioInputStream(convertFormat, rawSourceStream);
					AudioInputStream convertStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream)
			) {
				int read;
				final byte[] buffer = new byte[8192];
				
				while ((read = convertStream.read(buffer, 0, buffer.length)) >= 0) {
					output.write(buffer, 0, read);
				}
			}
		}
	}
	
	private AudioFormat getTargetFormat() {
		return this.audioFormat == null
				       ? new AudioFormat(44100, 8, 1, true, false)
				       : audioFormat;
	}
	
	private AudioFormat getAudioFormat(AudioFormat sourceFormat) {
		return new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				sourceFormat.getSampleRate(),
				16,
				sourceFormat.getChannels(),
				sourceFormat.getChannels() * 2,
				sourceFormat.getSampleRate(),
				false
		);
	}
}