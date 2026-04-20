package effects.audio.exceptions;

public class ConversionException extends RuntimeException {
	public ConversionException(Exception cause) {
		super(cause);
	}
	
	public ConversionException(String message) {
		super(message);
	}
}