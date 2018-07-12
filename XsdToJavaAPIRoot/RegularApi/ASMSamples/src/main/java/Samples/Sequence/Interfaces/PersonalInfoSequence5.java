package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.Country;
import Samples.Sequence.Classes.PersonalInfoComplete;

public interface PersonalInfoSequence5<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default P country(String value){
        º().addChild(new Country<>(self()).text(value));
        PersonalInfoComplete<P> var = new PersonalInfoComplete<>(º(), "personalInfo");
        this.getChildren().forEach(var::addChild);
        return º();
    }

}
