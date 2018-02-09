package Samples.HTML;

public class AbstractAttribute<T> implements IAttribute<T> {

    private T value;
    private String name;

    public AbstractAttribute(T value){
        this.value = value;
        this.name = getDefaultName(this);
    }

    public AbstractAttribute(T value, String name){
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

    static String getDefaultName(AbstractAttribute attribute){
        String simpleName = attribute.getClass().getSimpleName().replace("Attr", "");

        return simpleName.toLowerCase().charAt(0) + simpleName.substring(1);
    }
}
