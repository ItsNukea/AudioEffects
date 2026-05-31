package effects.audio.effectoperations;

import effects.audio.gui.menus.LoadingScreen;

import java.io.*;
import java.nio.channels.*;

public abstract class EffectOperation {
    protected File file;
    private FileOutputStream fos;
    private FileChannel channel;
    private FileLock lock;

    protected void lock(File file) {
        if (lock != null) {
            throw new IllegalStateException("A file is already locked");
        }

        try {
            fos = new FileOutputStream(file);
            channel = fos.getChannel();
            lock = channel.lock();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void unlock() {
        if (lock == null) {
            throw new IllegalStateException("No file is currently locked");
        }

        try {
            lock.release();
            channel.close();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lock = null;
        channel = null;
        fos = null;
    }

    protected abstract void start(LoadingScreen screen);
}
