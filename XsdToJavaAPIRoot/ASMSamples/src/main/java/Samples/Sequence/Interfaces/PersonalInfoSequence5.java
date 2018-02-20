package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence5<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {

    default PersonalInfoComplete<P> country(String value){
        PersonalInfoComplete<P> obj = new PersonalInfoComplete<>(this.º(), "personInfo");
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new Country<>(this.self()).text(value));
        return obj;
    }

}
