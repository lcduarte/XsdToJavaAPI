package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.PersonalInfoComplete;

public interface PersonalInfoSequence6<T extends IElement> extends IElement<T> {

    default PersonalInfoComplete country() {
        PersonalInfoComplete obj = new PersonalInfoComplete();
        getChildren().forEach(obj::addChild);
        obj.addChild(country());
        return obj;
    }
}
