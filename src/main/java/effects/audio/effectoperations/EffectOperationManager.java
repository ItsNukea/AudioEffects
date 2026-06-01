package effects.audio.effectoperations;

import effects.audio.effectoperations.operations.*;
import effects.audio.gui.menus.LoadingScreen;

public class EffectOperationManager {
	public static void init(LoadingScreen screen) {
		switch (screen.selectedEffect) {
			case ECHO -> new EchoOperation().start(screen, screen.selectedEffect.getParameters());
			case EARRAPE -> new EarrapeOperation().start(screen, screen.selectedEffect.getParameters());
			default -> throw new UnsupportedOperationException("Code for effect type " + screen.selectedEffect.getPrettyName() + " not implemented yet!");
		}
	}
}