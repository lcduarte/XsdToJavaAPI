package Samples.HTML;

public enum EnumExample implements EnumInterface<String>{

    MONDAY ("Monday"),
    TUESDAY ("Tuesday"),
    WEDNESDAY ("Wednesday"),
    THURSDAY ("Thursday"),
    FRIDAY ("Friday"),
    SATURDAY ("Saturday"),
    SUNDAY ("Sunday");

    private final String value;

    EnumExample(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}