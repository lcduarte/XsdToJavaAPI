package samples.sequence;

import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class Country<P extends Element> implements TextGroup<Country<P>, P> {

    private Visitor visitor;
    private P parent;

    public Country(Visitor visitor, P parent) {
        this.visitor = visitor;
        this.parent = parent;
        //visitor.visitElementFirstName(this);
    }

    @Override
    public Country<P> self() {
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
        return "country";
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }
}
