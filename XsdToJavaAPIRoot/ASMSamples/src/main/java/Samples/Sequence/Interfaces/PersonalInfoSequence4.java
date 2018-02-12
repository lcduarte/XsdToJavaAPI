package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence4<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {

    default PersonalInfoCity city(String value){
        PersonalInfoCity obj = new PersonalInfoCity();
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new City().text(value));
        return obj;
    }

}
