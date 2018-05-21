package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.PersonalInfoSequence3;

public class PersonalInfoLastName<P extends Element> extends AbstractElement<PersonalInfoLastName<P>, P> implements PersonalInfoSequence3<PersonalInfoLastName<P>, P> {

    public PersonalInfoLastName(P parent, String personalInfo) {
        super(parent, personalInfo);
    }

    @Override
    public PersonalInfoLastName<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoLastName<P> cloneElem() {
        return null;
    }
}

