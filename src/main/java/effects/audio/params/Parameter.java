package effects.audio.params;

public class Parameter<V> {
    public final String key;
    public final String prettyName;
    public V value;
    public final Class<?> valueClass;


    public Parameter(String key, String prettyName, V value) {
        this.key = key;
        this.value = value;
        this.prettyName = prettyName;
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

    public Double getDouble() {
        if(valueClass != Double.class) throw new IllegalArgumentException("Value is not a double");
        return (Double) value;
    }

    public Boolean getBoolean() {
        if(valueClass != Boolean.class) throw new IllegalArgumentException("Value is not a boolean");
        return (Boolean) value;
    }
}
