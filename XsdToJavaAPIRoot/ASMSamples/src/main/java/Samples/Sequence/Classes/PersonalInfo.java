package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.Sequence.Interfaces.PersonalInfoSequence1;
import Samples.HTML.Visitor;

public class PersonalInfo<P extends Element> extends AbstractElement<PersonalInfo<P>, P> implements PersonalInfoSequence1<PersonalInfo<P>, P> {

    public PersonalInfo(P parent, String personalInfo) {
        super(parent, personalInfo);
    }

    @Override
    public PersonalInfo<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfo<P> cloneElem() {
        return null;
    }
}
