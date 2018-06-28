package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;

public class PersonalInfoComplete<P extends Element> extends AbstractElement<PersonalInfoComplete<P>, P> {

    public PersonalInfoComplete(P parent, int depth) {
        super(parent, "personalInfo", depth);
    }

    @Override
    public PersonalInfoComplete<P> self() {
        return this;
    }

    @Override
    public P º() {
        return super.parent;
    }
}
