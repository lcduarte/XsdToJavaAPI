package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;

public class PersonalInfoLastName<P extends AbstractElement> extends AbstractElement {

    public static <P extends AbstractElement> PersonalInfoAddress<P> address(String value){
        visitor.visitElement("address");
        visitor.visitText(value);
        visitor.visitParent("address");
        return null;
    }
}

