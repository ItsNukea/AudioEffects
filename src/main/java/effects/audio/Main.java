package effects.audio;

import javafx.application.Platform;
import org.slf4j.*;

import javax.sound.sampled.AudioSystem;
import java.util.Arrays;

public class Main {
	public static final Logger LOGGER = LoggerFactory.getLogger("Audio Effects");

	static void main() {
		Window.init();
		Platform.startup(() -> {});
		LOGGER.debug(Arrays.toString(AudioSystem.getAudioFileTypes()));
	}
}