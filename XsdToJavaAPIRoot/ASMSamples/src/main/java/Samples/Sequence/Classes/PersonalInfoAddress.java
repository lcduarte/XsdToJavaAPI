package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.IElement;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.PersonalInfoSequence1;
import Samples.Sequence.Interfaces.PersonalInfoSequence2;
import Samples.Sequence.Interfaces.PersonalInfoSequence4;

public class PersonalInfoAddress<P extends IElement> extends AbstractElement<PersonalInfoAddress<P>, P> implements PersonalInfoSequence4<PersonalInfoAddress<P>, P> {
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
