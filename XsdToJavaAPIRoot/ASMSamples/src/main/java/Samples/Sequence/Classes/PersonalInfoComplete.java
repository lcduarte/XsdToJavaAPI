package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.Visitor;

public class PersonalInfoComplete<P extends Element> extends AbstractElement<PersonalInfoComplete<P>, P> {

    public PersonalInfoComplete(P parent, String personalInfo) {
        super(parent, personalInfo);
    }

    @Override
    public PersonalInfoComplete<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public PersonalInfoComplete<P> cloneElem() {
        return null;
    }
}
