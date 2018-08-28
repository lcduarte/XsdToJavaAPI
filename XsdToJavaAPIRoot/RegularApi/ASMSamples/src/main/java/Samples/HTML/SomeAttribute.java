package Samples.HTML;

public class SomeAttribute extends BaseAttribute<Double> {

    SomeAttribute(Double value) {
        super(value, "SomeAttribute");

        RestrictionValidator.validateMaxInclusive(9.999999999999E11D, value);
        RestrictionValidator.validateMinInclusive(9.99999999999E11D, value);
    }

}
