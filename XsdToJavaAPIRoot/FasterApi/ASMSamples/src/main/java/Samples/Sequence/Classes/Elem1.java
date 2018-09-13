package Samples.Sequence.Classes;

import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class Elem1<P extends Element> implements TextGroup<Elem1<P>, P> {

    private Visitor visitor;
    private P parent;

    public Elem1(Visitor visitor, P parent) {
        this.visitor = visitor;
        this.parent = parent;
        //visitor.visitElementFirstName(this);
    }

    @Override
    public Elem1<P> self() {
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
        return "elem1";
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }
}
