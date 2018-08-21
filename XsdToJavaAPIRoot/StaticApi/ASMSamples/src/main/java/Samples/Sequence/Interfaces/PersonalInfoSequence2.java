package Samples.Sequence.Interfaces;

import Samples.HTML.AbstractElement;
import Samples.Sequence.Classes.PersonalInfoLastName;

public interface PersonalInfoSequence2 {

    static <P extends AbstractElement> PersonalInfoLastName<P> lastName(String value){
        AbstractElement.visitor.visitElement("lastName");
        AbstractElement.visitor.visitText(value);
        AbstractElement.visitor.visitParent("lastName");
        return null;
    }

}
