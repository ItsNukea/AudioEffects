package effects.audio.effectoperations;

import effects.audio.*;
import effects.audio.gui.menus.*;

import java.io.File;

public class EffectOperationManager {

	public static void init() {
		MainMenu menu = (MainMenu) (Window.getPanel());
		File selectedFile = menu.getSelectedFile();
		EffectType selectedEffectType = menu.getSelectedEffect();

        ParameterMenu pmenu = new ParameterMenu(selectedEffectType);
		Window.setWindow(pmenu);
	}
}