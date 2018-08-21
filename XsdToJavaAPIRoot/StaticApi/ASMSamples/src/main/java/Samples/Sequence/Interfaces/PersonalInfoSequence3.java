package Samples.Sequence.Interfaces;

import Samples.HTML.AbstractElement;
import Samples.Sequence.Classes.PersonalInfoAddress;

public interface PersonalInfoSequence3 {

    static <P extends AbstractElement> PersonalInfoAddress<P> address(String value){
        AbstractElement.visitor.visitElement("address");
        AbstractElement.visitor.visitText(value);
        AbstractElement.visitor.visitParent("address");
        return null;
    }

}
