package effects.audio.io;

import effects.audio.Main;
import javafx.application.Platform;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class FileSelector {
	public static File get() {
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
				chooser.setInitialDirectory(Paths.get(System.getProperty("user.home"), "Downloads").toFile());

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
        Main.LOGGER.info("Selected file: {}", file == null ? "null" : file.getName());
		return file;
	}
}