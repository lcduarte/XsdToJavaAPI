package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.IElement;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.PersonalInfoSequence1;
import Samples.Sequence.Interfaces.PersonalInfoSequence2;
import Samples.Sequence.Interfaces.PersonalInfoSequence5;

public class PersonalInfoFirstName<P extends IElement> extends AbstractElement<PersonalInfoFirstName<P>, P> implements PersonalInfoSequence2<PersonalInfoFirstName<P>, P> {
    @Override
    public PersonalInfoFirstName self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoFirstName cloneElem() {
        return null;
    }
}
