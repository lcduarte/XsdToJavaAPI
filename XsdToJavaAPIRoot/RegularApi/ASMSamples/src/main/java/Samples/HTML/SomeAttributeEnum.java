package Samples.HTML;

public class SomeAttributeEnum extends BaseAttribute<String> {

    SomeAttributeEnum(EnumExample val) {
        super(val.getValue(), "SomeAttributeEnum");

        String value = val.getValue();

        RestrictionValidator.validateMaxLength(10, value);
        RestrictionValidator.validateMinLength(1, value);
    }

}