package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Visitor;
import Samples.Sequence.Interfaces.PersonalInfoSequence5;

public class PersonalInfoCity extends AbstractElement<PersonalInfoCity> implements PersonalInfoSequence5 {
    @Override
    public PersonalInfoCity self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoCity cloneElem() {
        return null;
    }
}

