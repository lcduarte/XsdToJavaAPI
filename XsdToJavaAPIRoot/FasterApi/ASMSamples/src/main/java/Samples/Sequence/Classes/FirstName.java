package Samples.Sequence.Classes;

import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class FirstName<P extends Element> implements TextGroup<FirstName<P>, P> {

    private Visitor visitor;
    private P parent;

    public FirstName(P parent, Visitor visitor) {
        this.visitor = visitor;
        this.parent = parent;
        //visitor.visitElementFirstName(this);
    }

    @Override
    public FirstName<P> self() {
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
        return "firstName";
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }
}
