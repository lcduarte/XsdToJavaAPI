package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.LastName;
import Samples.Sequence.Classes.PersonalInfoLastName;

public interface PersonalInfoSequence2<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default PersonalInfoLastName<P> lastName(String value){
        º().addChild(new LastName<>(self()).text(value));
        PersonalInfoLastName<P> var = new PersonalInfoLastName<>(º(), "personalInfo");
        this.getChildren().forEach(var::addChild);
        return var;
    }

}
