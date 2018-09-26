package samples.sequence;

import samples.html.AbstractElement;
import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

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