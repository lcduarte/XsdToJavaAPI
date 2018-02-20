package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.FirstName;
import Samples.Sequence.Classes.PersonalInfoFirstName;
import Samples.Sequence.Classes.PersonalInfo;

public interface PersonalInfoSequence1<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {

    default PersonalInfoFirstName<P> firstName(String value){
        PersonalInfoFirstName<P> obj = new PersonalInfoFirstName<>(this.º(), "personalInfo");
        this.getChildren().forEach(obj::addChild);
        obj.addChild(new FirstName<>(this.self()).text(value));

        if (this.º() != null){
            this.º().getChildren().remove(this);
            this.º().addChild(this);
        }
        return obj;
    }

    /*
    default PersonalInfoFirstName<P> firstName(String value){
        PersonalInfoFirstName<P> obj = new PersonalInfoFirstName<>(this.getParent(), "personalInfo");
        this.getChildren().forEach(obj::addChild);
        obj.addChild(new FirstName<>(this.self()).text(value));
        return obj;
    }*/

}
