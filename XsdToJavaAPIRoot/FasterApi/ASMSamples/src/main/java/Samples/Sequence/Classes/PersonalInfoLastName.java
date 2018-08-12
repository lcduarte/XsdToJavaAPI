package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.Sequence.Interfaces.PersonalInfoSequence3;

public class PersonalInfoLastName<P extends Element> extends AbstractElement<PersonalInfoLastName<P>, P> {

    public PersonalInfoLastName(P parent) {
        super(parent, "personalInfo");
    }

    @Override
    public PersonalInfoLastName<P> self() {
        return this;
    }

    public PersonalInfoAddress<P> address(String value){
        new Address<>(this.self()).text(value).º();
        return new PersonalInfoAddress<>(parent);
    }
}

