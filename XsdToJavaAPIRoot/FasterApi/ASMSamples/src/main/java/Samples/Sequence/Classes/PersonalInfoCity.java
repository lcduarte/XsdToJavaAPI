package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.Sequence.Interfaces.PersonalInfoSequence5;

public class PersonalInfoCity<P extends Element> extends AbstractElement<PersonalInfoCity<P>, P> {

    public PersonalInfoCity(P parent) {
        super(parent, "personalInfo");
    }

    @Override
    public PersonalInfoCity<P> self() {
        return this;
    }

    public PersonalInfoComplete<P> country(String value){
        new Country<>(this.self()).text(value).º();
        return new PersonalInfoComplete<>(parent);
    }
}

