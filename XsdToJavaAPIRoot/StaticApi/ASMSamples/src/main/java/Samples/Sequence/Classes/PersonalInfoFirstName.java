package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;

public class PersonalInfoFirstName<P extends AbstractElement> extends AbstractElement {

    public static <P extends AbstractElement> PersonalInfoLastName<P> lastName(String value){
        AbstractElement.visitor.visitElement("lastName");
        AbstractElement.visitor.visitText(value);
        AbstractElement.visitor.visitParent("lastName");
        return null;
    }

}
