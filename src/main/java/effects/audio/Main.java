package effects.audio;

import javax.sound.sampled.AudioSystem;
import java.util.Arrays;

public class Main {
	static void main() {
		Window.init();
		System.out.println(Arrays.toString(AudioSystem.getAudioFileTypes()));
	}
}