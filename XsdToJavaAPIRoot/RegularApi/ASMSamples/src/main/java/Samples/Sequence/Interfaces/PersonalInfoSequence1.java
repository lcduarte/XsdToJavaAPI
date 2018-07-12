package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.FirstName;
import Samples.Sequence.Classes.PersonalInfoFirstName;

public interface PersonalInfoSequence1<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default PersonalInfoFirstName<T> firstName(String value){
        addChild(new FirstName<>(self()).text(value));
        PersonalInfoFirstName<T> var = new PersonalInfoFirstName<>(self(), "personalInfo");
        this.getChildren().forEach(var::addChild);
        return var;
    }

    /*
     default PersonalInfoFirstName<T> firstName(String value){
        PersonalInfoFirstName<T> obj = new PersonalInfoFirstName<>(self(), "personalInfo");
        this.getChildren().forEach(obj::addChild);

        FirstName<T> f = new FirstName<>(this.self()).text(value);

        obj.º().addChild(f);
        obj.addChild(f);

        return obj;
    }
     */
}
