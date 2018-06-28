package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.Sequence.Interfaces.PersonalInfoSequence3;

public class PersonalInfoLastName<P extends Element> extends AbstractElement<PersonalInfoLastName<P>, P> implements PersonalInfoSequence3<PersonalInfoLastName<P>, P> {

    public PersonalInfoLastName(P parent, int depth) {
        super(parent, "personalInfo", depth);
    }

    @Override
    public PersonalInfoLastName<P> self() {
        return this;
    }

}

