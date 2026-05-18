package effects.audio.params;

public class Parameter<V> {
    public final String key;
    public V value;
    public final Class<?> valueClass;


    public Parameter(String key, V value) {
        this.key = key;
        this.value = value;
        this.valueClass = value.getClass();
    }
}
