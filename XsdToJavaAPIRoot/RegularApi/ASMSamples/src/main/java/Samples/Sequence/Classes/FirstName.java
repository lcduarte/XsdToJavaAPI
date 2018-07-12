package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class FirstName<P extends Element> extends AbstractElement<FirstName<P>, P> implements TextGroup<FirstName<P>, P> {

    public FirstName(P parent){
        super(parent, "firstName");
    }

    @Override
    public FirstName<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public FirstName<P> cloneElem() {
        return null;
    }
}

