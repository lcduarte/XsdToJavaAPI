package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.IElement;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.PersonalInfoSequence5;

public class PersonalInfoComplete<P extends IElement> extends AbstractElement<PersonalInfoComplete<P>, P> {
    @Override
    public PersonalInfoComplete self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoComplete cloneElem() {
        return null;
    }
}
