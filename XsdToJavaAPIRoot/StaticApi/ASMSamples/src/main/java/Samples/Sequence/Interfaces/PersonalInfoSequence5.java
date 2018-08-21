package Samples.Sequence.Interfaces;

import Samples.HTML.AbstractElement;
import Samples.Sequence.Classes.PersonalInfoComplete;

public interface PersonalInfoSequence5 {

    static <P extends AbstractElement> PersonalInfoComplete<P> country(String value){
        AbstractElement.visitor.visitElement("country");
        AbstractElement.visitor.visitText(value);
        AbstractElement.visitor.visitParent("country");
        return null;
    }

}
