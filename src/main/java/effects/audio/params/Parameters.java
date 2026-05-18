package effects.audio.params;

import effects.audio.exceptions.DuplicateException;

import java.util.*;

public class Parameters {
    private final HashSet<Parameter<?>> parameters = new HashSet<>();

    private Parameters(HashMap<String, Object> map) {
        map.forEach((key, value) -> {
            if (!this.parameters.add(new Parameter<>(key, value))) {
                throw new DuplicateException("Duplicate parameter key: " + key);
            }
        });
    }

    public static Parameters of(ParameterSupplier supplier) {
        HashMap<String, Object> parameters = new HashMap<>();
        supplier.extractParameters(parameters);
        return new Parameters(parameters);
    }

    public Parameter<?> get(String key) {
        for (Parameter<?> parameter : parameters) {
            if(parameter.key.equals(key)) {
                return parameter;
            }
        }
        return null;
    }

    public int size() {
        return parameters.size();
    }
}
