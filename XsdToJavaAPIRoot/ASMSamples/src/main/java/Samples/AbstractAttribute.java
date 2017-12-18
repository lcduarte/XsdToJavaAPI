package Samples;

public class AbstractAttribute<T> implements IAttribute<T> {

    private T value;

    AbstractAttribute(T value){
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

}
