package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.PersonalInfoSequence2;
import Samples.Sequence.Interfaces.PersonalInfoSequence3;

public class PersonalInfoLastName extends AbstractElement<PersonalInfoLastName> implements PersonalInfoSequence3 {
    @Override
    public PersonalInfoLastName self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoLastName cloneElem() {
        return null;
    }
}

