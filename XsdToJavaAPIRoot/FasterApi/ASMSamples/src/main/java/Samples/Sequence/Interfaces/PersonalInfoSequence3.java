package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.Address;
import Samples.Sequence.Classes.PersonalInfoAddress;

public interface PersonalInfoSequence3<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default PersonalInfoAddress<P> address(String value){
        new Address<>(this.self()).text(value).º();
        return new PersonalInfoAddress<>(getParent());
    }

}
