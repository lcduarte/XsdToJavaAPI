package samples.html;

public final class SomeAttribute {

    public static void validateRestrictions(Float value) {
        RestrictionValidator.validateMaxInclusive(99999999999999D, value);
    }

    private SomeAttribute(){

    }
}
