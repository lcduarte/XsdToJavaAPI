package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class Elem2<P extends Element> extends AbstractElement<Elem2<P>, P> implements TextGroup<Elem2<P>, P> {

    public Elem2(P parent){
        super(parent, "address");
    }

    @Override
    public Elem2<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public Elem2<P> cloneElem() {
        return null;
    }
}