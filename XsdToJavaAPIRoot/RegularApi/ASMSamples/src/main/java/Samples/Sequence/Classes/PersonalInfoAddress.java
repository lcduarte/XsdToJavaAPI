package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.PersonalInfoSequence4;

public class PersonalInfoAddress<P extends Element> extends AbstractElement<PersonalInfoAddress<P>, P> implements PersonalInfoSequence4<PersonalInfoAddress<P>, P> {

    public PersonalInfoAddress(P parent, String personalInfo) {
        super(parent, personalInfo);
    }

    @Override
    public PersonalInfoAddress<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoAddress<P> cloneElem() {
        return null;
    }
}
