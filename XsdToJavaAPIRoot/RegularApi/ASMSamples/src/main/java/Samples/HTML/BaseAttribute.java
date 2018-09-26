package samples.html;

public class BaseAttribute<T> implements IAttribute<T> {

    private T value;
    private String name;

    public BaseAttribute(T value, String name){
        this.value = value;
        this.name = name;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

}
