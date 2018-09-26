package samples.sequence;

import samples.html.AbstractElement;
import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class Elem1<P extends Element> extends AbstractElement<Elem1<P>, P> implements TextGroup<Elem1<P>, P> {

    public Elem1(P parent){
        super(parent, "address");
    }

    @Override
    public Elem1<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public Elem1<P> cloneElem() {
        return null;
    }
}