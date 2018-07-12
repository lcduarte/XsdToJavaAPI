package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.City;
import Samples.Sequence.Classes.PersonalInfoCity;

public interface PersonalInfoSequence4<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default PersonalInfoCity<P> city(String value){
        º().addChild(new City<>(self()).text(value));
        PersonalInfoCity<P> var = new PersonalInfoCity<>(º(), "personalInfo");
        this.getChildren().forEach(var::addChild);
        return var;
    }

}
