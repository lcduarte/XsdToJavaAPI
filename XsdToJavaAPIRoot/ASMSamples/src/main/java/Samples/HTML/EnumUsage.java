package Samples.HTML;

public class EnumUsage<String> extends AbstractAttribute<String>{

    public EnumUsage(EnumExample value){
        super(((EnumInterface<String>)value).getValue());
    }
}
