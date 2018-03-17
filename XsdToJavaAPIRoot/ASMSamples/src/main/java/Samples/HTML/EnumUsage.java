package Samples.HTML;

public class EnumUsage<String> extends BaseAttribute<String> {

    public EnumUsage(EnumExample value){
        super(((EnumInterface<String>)value).getValue());
    }
}
