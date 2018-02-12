package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence3<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {

    default PersonalInfoAddress address(String value){
        PersonalInfoAddress obj = new PersonalInfoAddress();
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new Address().text(value));
        return obj;
    }

}
