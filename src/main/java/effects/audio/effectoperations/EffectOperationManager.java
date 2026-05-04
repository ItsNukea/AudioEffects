package effects.audio.effectoperations;

import effects.audio.EffectTypes;
import effects.audio.gui.menus.MainMenu;
import effects.audio.util.OperationSuccessStatus;

import java.io.File;

import static effects.audio.util.OperationSuccessStatus.*;

public class EffectOperationManager {
	public static OperationSuccessStatus successStatus;
	
	public static void init(MainMenu menu) {
		File selectedFile = menu.getSelectedFile();
		EffectTypes selectedEffectType = menu.getSelectedEffect();
		
		if(selectedFile == null) {
			successStatus = FILE_NOT_SELECTED;
			return;
		}
		if(selectedEffectType == null) {
			successStatus = EFFECT_TO_APPLY_NOT_SELECTED;
			return;
		}
		
		switch(selectedEffectType) {
			case EARRAPE -> {}
			case ECHO -> {}
		}
	}
}