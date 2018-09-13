package Samples.Sequence.Classes;

import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class Address<P extends Element> implements TextGroup<Address<P>, P> {

    private Visitor visitor;
    private P parent;

    public Address(Visitor visitor, P parent) {
        this.visitor = visitor;
        this.parent = parent;
        //visitor.visitElementFirstName(this);
    }

    @Override
    public Address<P> self() {
        return this;
    }

    @Override
    public P º() {
        //visitor.visitParentFirstName(this);
        return parent;
    }

    @Override
    public P getParent() {
        return parent;
    }

    @Override
    public String getName() {
        return "address";
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }
}
