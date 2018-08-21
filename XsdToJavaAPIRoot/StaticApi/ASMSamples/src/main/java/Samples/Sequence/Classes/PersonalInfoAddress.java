package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.Sequence.Interfaces.PersonalInfoSequence4;

public class PersonalInfoAddress<P extends AbstractElement> extends AbstractElement {

    public static <P extends AbstractElement> PersonalInfoCity<P> city(String value){
        visitor.visitElement("city");
        visitor.visitText(value);
        visitor.visitParent("city");
        return null;
    }

}
