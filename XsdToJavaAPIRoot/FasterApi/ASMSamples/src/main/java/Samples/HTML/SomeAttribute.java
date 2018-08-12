package Samples.HTML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class SomeAttribute extends BaseAttribute<String> {

    static {
        Map<java.lang.String, Object> restriction1 = new HashMap<>();

        restriction1.put("Length", 2);
        restriction1.put("MaxExclusive", 20);
        restriction1.put("MinExclusive", 15);
        restriction1.put("Pattern" , "This isn't really a pattern.");

        java.util.List<java.lang.String> enumeration = new ArrayList<>();

        enumeration.add("String1");
        enumeration.add("String2");

        restriction1.put("Enumeration", enumeration);

        restrictions.add(restriction1);

        Map<java.lang.String, Object> restriction2 = new HashMap<>();

        restriction2.put("Pattern", "Still not a pattern");

        restrictions.add(restriction2);
    }

    SomeAttribute(String value) {
        super(value, "SomeAttribute");
    }
}
