package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Visitor;

public class PersonalInfoComplete extends AbstractElement<PersonalInfoComplete>  {
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
