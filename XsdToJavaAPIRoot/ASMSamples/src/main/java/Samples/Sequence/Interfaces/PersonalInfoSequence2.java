package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence2<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {

    default PersonalInfoLastName lastName(String value){
        PersonalInfoLastName obj = new PersonalInfoLastName();
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new LastName().text(value));
        return obj;
    }

}
