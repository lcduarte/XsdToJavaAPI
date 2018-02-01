package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.PersonalInfoSequence1;
import Samples.Sequence.Interfaces.PersonalInfoSequence2;

public class PersonalInfoFirstName extends AbstractElement<PersonalInfoFirstName> implements PersonalInfoSequence2 {
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
