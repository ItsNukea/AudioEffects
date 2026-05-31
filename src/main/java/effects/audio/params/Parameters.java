package effects.audio.params;

import effects.audio.exceptions.DuplicateException;

import java.util.*;

public class Parameters implements Collection<Parameter<?>> {
    private final HashSet<Parameter<?>> parameters = new HashSet<>();
    private final String OPERATION_NOT_SUPPORTED = "Operation not supported";
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

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public boolean contains(Object o) {
        return parameters.contains(o);
    }

    @Override
    public Iterator<Parameter<?>> iterator() {
        return parameters.iterator();
    }

    @Override
    public Object[] toArray() {
        return parameters.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return parameters.toArray(ts);
    }

    @Override
    public boolean add(Parameter<?> parameter) {
        throw new UnsupportedOperationException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return parameters.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends Parameter<?>> collection) {
        throw new UnsupportedOperationException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        throw new UnsupportedOperationException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        throw new UnsupportedOperationException(OPERATION_NOT_SUPPORTED);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(OPERATION_NOT_SUPPORTED);
    }
}
