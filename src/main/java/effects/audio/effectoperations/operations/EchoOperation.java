package effects.audio.effectoperations.operations;

import effects.audio.effectoperations.EffectOperation;
import effects.audio.gui.menus.LoadingScreen;

public class EchoOperation extends EffectOperation {
    public EchoOperation() {}

    @Override
    public void start(LoadingScreen screen) {
        file = screen.selectedFile;
        lock(file);

        unlock();
    }
}