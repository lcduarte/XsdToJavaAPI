package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.Sequence.Interfaces.PersonalInfoSequence4;

public class PersonalInfoAddress<P extends Element> extends AbstractElement<PersonalInfoAddress<P>, P> {

    public PersonalInfoAddress(P parent) {
        super(parent, "personalInfo");
    }

    @Override
    public PersonalInfoAddress<P> self() {
        return this;
    }

    public PersonalInfoCity<P> city(String value){
        new City<>(this.self()).text(value).º();
        return new PersonalInfoCity<>(parent);
    }

}
