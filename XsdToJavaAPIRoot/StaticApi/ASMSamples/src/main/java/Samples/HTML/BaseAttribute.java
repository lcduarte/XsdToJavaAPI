package Samples.HTML;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class BaseAttribute {

    private final static Map<Class, BiConsumer<Map<String, Object>, Object>> mapper;

    static {
        mapper = new HashMap<>();

        mapper.put(String.class, RestrictionValidator::validateString);
        mapper.put(Double.class, RestrictionValidator::validateNumeric);
        mapper.put(List.class, RestrictionValidator::validateList);
    }

    @SuppressWarnings("WeakerAccess")
    protected static <T> void validateRestriction(Map<String, Object> restriction, T value){
        BiConsumer<Map<String, Object>, Object> validationFunction = mapper.get(value.getClass());

        if (validationFunction != null){
            validationFunction.accept(restriction, value);
        }
    }
}
