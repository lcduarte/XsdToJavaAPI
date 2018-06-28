package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence5<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default PersonalInfoComplete<P> country(String value){
        PersonalInfoComplete<P> obj = new PersonalInfoComplete<>(this.º(), "personInfo");
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new Country<>(this.self()).text(value));
        return obj;
    }

}
