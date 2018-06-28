package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.Sequence.Interfaces.PersonalInfoSequence2;

public class PersonalInfoFirstName<P extends Element> extends AbstractElement<PersonalInfoFirstName<P>, P> implements PersonalInfoSequence2<PersonalInfoFirstName<P>, P> {

    public PersonalInfoFirstName(P parent, int depth) {
        super(parent, "personalInfo", depth);
    }

    @Override
    public PersonalInfoFirstName<P> self() {
        return this;
    }

}
