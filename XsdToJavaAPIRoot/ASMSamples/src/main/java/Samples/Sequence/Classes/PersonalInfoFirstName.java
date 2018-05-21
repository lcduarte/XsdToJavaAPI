package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.PersonalInfoSequence2;

public class PersonalInfoFirstName<P extends Element> extends AbstractElement<PersonalInfoFirstName<P>, P> implements PersonalInfoSequence2<PersonalInfoFirstName<P>, P> {
    public PersonalInfoFirstName(P parent, String personalInfo) {
        super(parent, personalInfo);
    }

    @Override
    public PersonalInfoFirstName<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoFirstName<P> cloneElem() {
        return null;
    }
}
