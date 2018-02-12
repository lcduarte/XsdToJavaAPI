package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence5<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {

    default PersonalInfoComplete country(String value){
        PersonalInfoComplete obj = new PersonalInfoComplete();
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new Country().text(value));
        return obj;
    }

}
