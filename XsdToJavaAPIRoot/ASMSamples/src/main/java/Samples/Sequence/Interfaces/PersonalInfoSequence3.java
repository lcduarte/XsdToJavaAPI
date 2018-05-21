package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.*;

public interface PersonalInfoSequence3<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default PersonalInfoAddress<P> address(String value){
        PersonalInfoAddress<P> obj = new PersonalInfoAddress<>(this.º(), "personInfo");
        this.self().getChildren().forEach(obj::addChild);
        obj.addChild(new Address<>(this.self()).text(value));
        return obj;
    }

}
