package Samples.HTML;

import java.util.List;

public final class SomeAttribute {

    public static void validateRestrictions(Integer value) {

        RestrictionValidator.validateMaxInclusive(5, value);
        //RestrictionValidator.validateMaxLength(2000000, value);
        //RestrictionValidator.validateMinLength(2000000, value);
    }

    private SomeAttribute(){}
}
