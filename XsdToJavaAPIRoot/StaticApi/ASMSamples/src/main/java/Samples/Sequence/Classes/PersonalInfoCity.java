package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.Sequence.Interfaces.PersonalInfoSequence5;

public class PersonalInfoCity<P> extends AbstractElement {

    public static <P extends AbstractElement> PersonalInfoComplete<P> country(String value){
        visitor.visitElement("country");
        visitor.visitText(value);
        visitor.visitParent("country");
        return null;
    }
}

