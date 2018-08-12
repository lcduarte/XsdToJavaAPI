package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.Country;
import Samples.Sequence.Classes.PersonalInfoComplete;

public interface PersonalInfoSequence5<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default PersonalInfoComplete<P> country(String value){
        new Country<>(this.self()).text(value).º();
        return new PersonalInfoComplete<>(getParent());
    }

}
