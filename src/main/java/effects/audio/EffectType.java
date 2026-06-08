package effects.audio;

import effects.audio.params.Parameters;

public enum EffectType {
	//Add different effect types here (echo, distortion, etc...)
	ECHO("Echo", Parameters.of((params, keyToPrettyNameMap) -> {
		params.put("delay", 0.75f);
		keyToPrettyNameMap.put("delay", "Delay between echos");
		params.put("exponentialDecay", 0.4f);
		keyToPrettyNameMap.put("exponentialDecay", "Exponential decay factor");
		params.put("repetitions", 5);
		keyToPrettyNameMap.put("repetitions", "Echo repetitions");
	})),
	EARRAPE("Earrape", Parameters.of((params, keyToPrettyNameMap) -> {
		params.put("amplifier", 2f);
		keyToPrettyNameMap.put("amplifier", "Randomness amplifier");
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