package samples.html;

public enum EnumExampleInt implements EnumInterface<Double>{

    MONDAY (100.0),
    TUESDAY (200.0),
    WEDNESDAY (300.0),
    THURSDAY (400.0),
    FRIDAY (500.0),
    SATURDAY (600.0),
    SUNDAY (700.0);

    private final Double value;

    EnumExampleInt(Double value){
        this.value = value;
    }

    @Override
    public final Double getValue(){
        return value;
    }
}
