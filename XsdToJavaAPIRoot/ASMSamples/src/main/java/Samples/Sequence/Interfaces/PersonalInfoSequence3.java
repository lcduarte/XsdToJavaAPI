package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence3 extends IElement<PersonalInfoLastName> {

    default PersonalInfoAddress address(String value){
        PersonalInfoAddress obj = new PersonalInfoAddress();
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new Address().text(value));
        return obj;
    }

}
