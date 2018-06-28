package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence4<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default PersonalInfoCity<P> city(String value){
        PersonalInfoCity<P> obj = new PersonalInfoCity<>(this.º(), "personInfo");
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new City<>(this.self()).text(value));
        return obj;
    }

}
