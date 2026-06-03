package effects.audio.params;

import java.util.HashMap;

@FunctionalInterface
public interface ParameterSupplier {
    void extractParameters(HashMap<String, Object> parameters, HashMap<String, String> keyToPrettyNameMap);
}
