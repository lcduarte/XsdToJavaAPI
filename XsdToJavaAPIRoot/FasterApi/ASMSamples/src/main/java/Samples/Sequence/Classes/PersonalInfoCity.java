package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.Sequence.Interfaces.PersonalInfoSequence5;

public class PersonalInfoCity<P extends Element> extends AbstractElement<PersonalInfoCity<P>, P> implements PersonalInfoSequence5<PersonalInfoCity<P>, P> {

    public PersonalInfoCity(P parent, int depth) {
        super(parent, "personalInfo", depth);
    }

    @Override
    public PersonalInfoCity<P> self() {
        return this;
    }

}

