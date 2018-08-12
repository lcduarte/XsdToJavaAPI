package Samples.HTML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class BaseAttribute<T> implements IAttribute<T> {

    final static List<Map<String, Object>> restrictions;
    private final static Map<Class, BiConsumer<Map<String, Object>, Object>> mapper;

    private final T value;
    private final String name;

    static {
        restrictions = new ArrayList<>();
        mapper = new HashMap<>();

        mapper.put(String.class, RestrictionValidator::validateString);
        mapper.put(Double.class, RestrictionValidator::validateNumeric);
        mapper.put(List.class, RestrictionValidator::validateList);
    }

    public BaseAttribute(T value, String name){
        this.value = value;
        this.name = name;
        restrictions.forEach(this::validateRestrictions);
    }

    @Override
    public final T getValue() {
        return value;
    }

    @Override
    public final String getName() {
        return name;
    }

    private void validateRestrictions(Map<String, Object> restriction){
        BiConsumer<Map<String, Object>, Object> validationFunction = mapper.get(value.getClass());

        if (validationFunction != null){
            validationFunction.accept(restriction, value);
        }
    }
}
