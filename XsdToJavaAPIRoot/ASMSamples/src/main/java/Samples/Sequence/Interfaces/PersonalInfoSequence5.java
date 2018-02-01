package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence5 extends IElement<PersonalInfoCity> {

    default PersonalInfoComplete country(String value){
        PersonalInfoComplete obj = new PersonalInfoComplete();
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new Country().text(value));
        return obj;
    }

}
