package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.Sequence.Interfaces.PersonalInfoSequence4;

public class PersonalInfoAddress<P extends Element> extends AbstractElement<PersonalInfoAddress<P>, P> implements PersonalInfoSequence4<PersonalInfoAddress<P>, P> {

    public PersonalInfoAddress(P parent, int depth) {
        super(parent, "personalInfo", depth);
    }

    @Override
    public PersonalInfoAddress<P> self() {
        return this;
    }

}
