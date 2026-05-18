package effects.audio;

import org.slf4j.*;

public class Main {
	public static final Logger LOGGER = LoggerFactory.getLogger("Audio Effects");

	static void main() {
		LOGGER.debug("(Debug message)");
		LOGGER.info("(Info message)");
		LOGGER.warn("(Warn message)");
		LOGGER.error("(Error message)");

		Window.init();
		//LOGGER.debug(Arrays.toString(AudioSystem.getAudioFileTypes()));
	}
}