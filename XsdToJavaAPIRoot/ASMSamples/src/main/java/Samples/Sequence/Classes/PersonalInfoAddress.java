package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.PersonalInfoSequence2;
import Samples.Sequence.Interfaces.PersonalInfoSequence4;

public class PersonalInfoAddress  extends AbstractElement<PersonalInfoAddress> implements PersonalInfoSequence4 {
    @Override
    public PersonalInfoAddress self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoAddress cloneElem() {
        return null;
    }
}
