package effects.audio;

import effects.audio.params.Parameters;

public enum EffectType {
	//Add different effect types here (echo, distortion, etc...)
	ECHO("Echo", Parameters.of(map -> {
		map.put("delay", 0.75f);
		map.put("exponentialDecay", 0.8f);
		map.put("repetitions", 5);
	})),
	EARRAPE("Earrape", Parameters.of(map -> {
		map.put("randomnessFactor", 1f);
	}));

	private final String prettyName;
	private final Parameters parameters;

	EffectType(String prettyName, Parameters parameters) {
		this.prettyName = prettyName;
		this.parameters = parameters;
	}

	public String getPrettyName() {
		return prettyName;
	}

	public Parameters getParameters() {
		return parameters;
	}
}