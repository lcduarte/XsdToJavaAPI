package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence2<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default PersonalInfoLastName<P> lastName(String value){
        PersonalInfoLastName<P> obj = new PersonalInfoLastName<P>(this.º(), "personInfo");
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new LastName<>(this.self()).text(value));
        return obj;
    }

}
