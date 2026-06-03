package effects.audio.effectoperations;

import effects.audio.effectoperations.operations.*;
import effects.audio.gui.menus.LoadingScreen;
import org.slf4j.*;

import javax.sound.sampled.*;
import java.io.*;

public class EffectOperationManager {
	public static Logger LOGGER = LoggerFactory.getLogger("Effect-Operation-Manager");

	public static void init(LoadingScreen screen) {
		File selectedFile = screen.selectedFile;
		String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf('.') + 1);

        Thread operationThread = new Thread(() -> {
            try {
                switch (screen.selectedEffect) {
                    case ECHO -> new EchoOperation(screen).start();
                    case EARRAPE -> new EarrapeOperation(screen).start();
                    default -> throw new UnsupportedOperationException("Code for effect type " + screen.selectedEffect.getPrettyName() + " not implemented yet!");
                }
            } catch (Throwable t) {
                LOGGER.error("Error occurred during effect operation", t);
            }
        });
		operationThread.setName("Effect Operation Thread");
		operationThread.start();
	}
}