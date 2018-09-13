package Samples.Sequence.Classes;

import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class City<P extends Element> implements TextGroup<City<P>, P> {

    private Visitor visitor;
    private P parent;

    public City(Visitor visitor, P parent) {
        this.visitor = visitor;
        this.parent = parent;
        //visitor.visitElementFirstName(this);
    }

    @Override
    public City<P> self() {
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
        return "city";
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }
}
