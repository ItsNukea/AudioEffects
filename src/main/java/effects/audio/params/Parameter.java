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

    public String getString() {
        if(valueClass != String.class) throw new IllegalArgumentException("Value is not a string");
        return (String) value;
    }

    public Integer getInt() {
        if(valueClass != Integer.class) throw new IllegalArgumentException("Value is not an integer");
        return (Integer) value;
    }

    public Float getFloat() {
        if(valueClass != Float.class) throw new IllegalArgumentException("Value is not a float");
        return (Float) value;
    }
}
