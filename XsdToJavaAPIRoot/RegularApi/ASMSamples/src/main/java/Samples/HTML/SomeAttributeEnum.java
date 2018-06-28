package Samples.HTML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SomeAttributeEnum extends BaseAttribute<EnumUsage> {

    private static List<Map<String, Object>> restrictions = new ArrayList<>();

    static {
        Map<java.lang.String, Object> restriction1 = new HashMap<>();
    }

    SomeAttributeEnum(EnumUsage value) {
        super(value, "SomeAttributeEnum");

        restrictions.forEach(restriction -> {
            Object toRestrictValue = SomeAttributeEnum.this.getValue();

            if (toRestrictValue instanceof String) {
                RestrictionValidator.validate(restriction, (String) toRestrictValue);
            }

            if (toRestrictValue instanceof Integer || toRestrictValue instanceof Short || toRestrictValue instanceof Float || toRestrictValue instanceof Double) {
                RestrictionValidator.validate(restriction, (Double) toRestrictValue);
            }

            if (toRestrictValue instanceof List) {
                RestrictionValidator.validate(restriction, (List) toRestrictValue);
            }
        });
    }
}