package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence4<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {

    default PersonalInfoCity<P> city(String value){
        PersonalInfoCity<P> obj = new PersonalInfoCity<>(this.getParent(), "personInfo");
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new City<>(this.self()).text(value));
        return obj;
    }

}
