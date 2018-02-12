package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.IElement;
import Samples.Sequence.Interfaces.PersonalInfoSequence1;
import Samples.HTML.Visitor;

public class PersonalInfo<P extends IElement> extends AbstractElement<PersonalInfo<P>, P> implements PersonalInfoSequence1<PersonalInfo<P>, P> {

    @Override
    public PersonalInfo self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfo cloneElem() {
        return null;
    }
}
