package Samples.Sequence.Interfaces;

import Samples.HTML.AbstractElement;
import Samples.Sequence.Classes.PersonalInfoCity;

public interface PersonalInfoSequence4 {

    static <P extends AbstractElement> PersonalInfoCity<P> city(String value){
        AbstractElement.visitor.visitElement("city");
        AbstractElement.visitor.visitText(value);
        AbstractElement.visitor.visitParent("city");
        return null;
    }

}
