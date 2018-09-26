package samples.sequence;

import samples.html.AbstractElement;
import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class Address<P extends Element> extends AbstractElement<Address<P>, P> implements TextGroup<Address<P>, P> {

    public Address(P parent){
        super(parent, "address");
    }

    @Override
    public Address<P> self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public Address<P> cloneElem() {
        return null;
    }
}
