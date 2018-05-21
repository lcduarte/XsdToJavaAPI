package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class LastName<P extends Element> extends AbstractElement<LastName<P>, P> implements TextGroup<LastName<P>, P> {

    public LastName(P parent){
        super(parent);
    }

    @Override
    public LastName<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public LastName<P> cloneElem() {
        return null;
    }
}

