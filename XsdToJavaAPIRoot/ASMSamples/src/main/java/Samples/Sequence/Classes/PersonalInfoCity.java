package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.IElement;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.PersonalInfoSequence4;
import Samples.Sequence.Interfaces.PersonalInfoSequence5;

public class PersonalInfoCity<P extends IElement> extends AbstractElement<PersonalInfoCity<P>, P> implements PersonalInfoSequence5<PersonalInfoCity<P>, P> {

    public PersonalInfoCity(P parent, String personalInfo) {
        super(parent, personalInfo);
    }

    @Override
    public PersonalInfoCity<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoCity<P> cloneElem() {
        return null;
    }
}

