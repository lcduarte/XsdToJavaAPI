package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.LastName;
import Samples.Sequence.Classes.PersonalInfoLastName;

public interface PersonalInfoSequence2<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default PersonalInfoLastName<P> lastName(String value){
        new LastName<>(this.self()).text(value).º();
        return new PersonalInfoLastName<>(getParent());
    }

}
