package Samples;

import XsdElements.XsdRestriction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SomeAttribute<String> extends AbstractAttribute<String>{

    private static List<Map<java.lang.String, Object>> restrictions = new ArrayList<>();

    static {
        Map<java.lang.String, Object> restriction1 = new HashMap<>();

        restriction1.put("Length", 2);
        restriction1.put("MaxExclusive", 20);
        restriction1.put("MinExclusive", 15);
        restriction1.put("Pattern" , "This isn't really a pattern.");

        List<java.lang.String> enumeration = new ArrayList<>();

        enumeration.add("String1");
        enumeration.add("String2");

        restriction1.put("Enumeration", enumeration);

        restrictions.add(restriction1);

        Map<java.lang.String, Object> restriction2 = new HashMap<>();

        restriction2.put("Pattern", "Still not a pattern");

        restrictions.add(restriction2);
    }

    public SomeAttribute(String value) {
        super(value);

        System.out.println(restrictions.size());

        restrictions.forEach(restriction -> {
            String toRestrictValue = SomeAttribute.this.getValue();

            if (toRestrictValue instanceof java.lang.String){
                RestrictionValidator.validate(restriction, (java.lang.String) toRestrictValue);
            }

            if (toRestrictValue instanceof Integer || toRestrictValue instanceof Short || toRestrictValue instanceof Float || toRestrictValue instanceof Double){
                RestrictionValidator.validate(restriction, (Double) toRestrictValue);
            }
        });
    }

}
