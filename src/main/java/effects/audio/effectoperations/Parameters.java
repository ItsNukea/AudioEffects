package effects.audio.effectoperations;

import java.util.HashMap;

public class Parameters {
    public HashMap<String, Object> parameters = new HashMap<>();

    private Parameters() {}

    public static Parameters create() {
        return new Parameters();
    }

    public void add(String key, Object value) {
        parameters.put(key, value);
    }

    public String getAsString(String key) {
        return (String) parameters.get(key);
    }

    public Object get(String key) {
        return parameters.get(key);
    }

    public int getAsInt(String key) {
        return (int) parameters.get(key);
    }

    public float getAsFloat(String key) {
        return (float) parameters.get(key);
    }

    public boolean getAsBoolean(String key) {
        return (boolean) parameters.get(key);
    }
}
