package Samples.HTML;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseAttribute<T> implements IAttribute<T> {

    protected static List<Map<String, Object>> restrictions = new ArrayList<>();

    private T value;
    private String name;

    public BaseAttribute(T value){
        this.value = value;
        this.name = getDefaultName(this);
        restrictions.forEach(this::validateRestrictions);
    }

    public BaseAttribute(T value, String name){
        this.value = value;
        this.name = name;
        restrictions.forEach(this::validateRestrictions);
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    static String getDefaultName(BaseAttribute attribute){
        String simpleName = attribute.getClass().getSimpleName().replace("Attr", "");

        return simpleName.toLowerCase().charAt(0) + simpleName.substring(1);
    }

    private void validateRestrictions(Map<String, Object> restriction){
        T toRestrictValue = getValue();

        if (toRestrictValue instanceof String){
            RestrictionValidator.validate(restriction, (String) toRestrictValue);
        }

        if (toRestrictValue instanceof Integer || toRestrictValue instanceof Short || toRestrictValue instanceof Float || toRestrictValue instanceof Double){
            RestrictionValidator.validate(restriction, (Double) toRestrictValue);
        }

        if (toRestrictValue instanceof List){
            RestrictionValidator.validate(restriction, (List) toRestrictValue);
        }
    }
}
