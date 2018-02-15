package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence2<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {

    default PersonalInfoLastName<P> lastName(String value){
        PersonalInfoLastName<P> obj = new PersonalInfoLastName<P>(this.getParent(), "personInfo");
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new LastName<>(this.self()).text(value));
        return obj;
    }

}
