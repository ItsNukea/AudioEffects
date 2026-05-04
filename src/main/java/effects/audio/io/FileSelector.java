package effects.audio.io;

import javafx.application.Platform;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class FileSelector {
	private static boolean javafxStarted = false;

	public static File get() {
		startJavaFXIfNeeded();

		AtomicReference<File> selectedFile = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			try {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Choose your audio file");

				chooser.getExtensionFilters().add(
						new FileChooser.ExtensionFilter(
								"Audio files",
								"*.wav",
								"*.wave",
								"*.aiff",
								"*.au",
								"*.mp3"
						)
				);

				selectedFile.set(chooser.showOpenDialog(null));
			} finally {
				latch.countDown();
			}
		});

		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}

		File file = selectedFile.get();
		System.out.println("Selected file: " + (file == null ? "null" : file.getName()));
		return file;
	}

	private static synchronized void startJavaFXIfNeeded() {
		if (!javafxStarted) {
			Platform.startup(() -> {});
			javafxStarted = true;
		}
	}
}