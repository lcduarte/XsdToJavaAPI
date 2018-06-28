package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class Address<P extends Element> extends AbstractElement<Address<P>, P> implements TextGroup<Address<P>, P> {

    public Address(Visitor visitor) {
        super(visitor, "address", 0);
        visitor.visit(this);
    }

    public Address(Visitor visitor, int depth) {
        super(visitor, "address", depth);
        visitor.visit(this);
    }

    public Address(P parent){
        super(parent, "address");
        visitor.visit(this);
    }

    public Address(P parent, String name){
        super(parent, name);
        visitor.visit(this);
    }

    @Override
    public Address<P> self() {
        return this;
    }

}
