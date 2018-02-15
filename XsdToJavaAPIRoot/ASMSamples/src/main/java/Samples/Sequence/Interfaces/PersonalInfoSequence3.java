package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence3<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {

    default PersonalInfoAddress<P> address(String value){
        PersonalInfoAddress<P> obj = new PersonalInfoAddress<>(this.getParent(), "personInfo");
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new Address<>(this.self()).text(value));
        return obj;
    }

}
