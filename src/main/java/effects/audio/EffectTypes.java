package effects.audio;

public enum EffectTypes {
	//Add different effect types here (echo, distortion, etc...)
	ECHO("Echo"), EARRAPE("Earrape");

	private final String name;

	EffectTypes(String name) {
		this.name = name;
	}

	public String getPrettyName() {
		return name;
	}
}