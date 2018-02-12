package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.FirstName;
import Samples.Sequence.Classes.PersonalInfoFirstName;
import Samples.Sequence.Classes.PersonalInfo;

public interface PersonalInfoSequence1<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {

    default PersonalInfoFirstName firstName(String value){
        PersonalInfoFirstName obj = new PersonalInfoFirstName();
        this.getChildren().forEach(obj::addChild);
        obj.addChild(new FirstName().text(value));
        return obj;
    }

}
