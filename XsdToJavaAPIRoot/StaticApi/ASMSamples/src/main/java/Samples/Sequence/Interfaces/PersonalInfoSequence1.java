package Samples.Sequence.Interfaces;

import Samples.HTML.AbstractElement;
import Samples.Sequence.Classes.PersonalInfoFirstName;

public interface PersonalInfoSequence1 {

    static <T extends AbstractElement> PersonalInfoFirstName<T> firstName(String value){
        AbstractElement.visitor.visitElement("firstName");
        AbstractElement.visitor.visitText(value);
        AbstractElement.visitor.visitParent("firstName");
        return null;
    }

    /*
    default PersonalInfoFirstName<P> firstName(String value){
        PersonalInfoFirstName<P> obj = new PersonalInfoFirstName<>(this.getParent(), "personalInfo");
        this.getChildren().forEach(obj::addChild);
        obj.addChild(new FirstName<>(this.self()).text(value));
        return obj;
    }*/

}
