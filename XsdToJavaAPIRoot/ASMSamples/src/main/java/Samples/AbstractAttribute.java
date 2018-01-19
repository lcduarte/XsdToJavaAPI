package Samples;

public abstract class AbstractAttribute<T> implements IAttribute<T> {

    private T value;
    private String name;

    AbstractAttribute(T value){
        this.value = value;

        String simpleName = getClass().getSimpleName().replace("Attr", "");

        this.name = simpleName.toLowerCase().charAt(0) + simpleName.substring(1);
    }

    @Override
    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
