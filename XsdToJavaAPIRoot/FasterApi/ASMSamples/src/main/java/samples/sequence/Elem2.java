package samples.sequence;

import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class Elem2<P extends Element> implements TextGroup<Elem2<P>, P> {

    private Visitor visitor;
    private P parent;

    public Elem2(Visitor visitor, P parent) {
        this.visitor = visitor;
        this.parent = parent;
        //visitor.visitElementFirstName(this);
    }

    @Override
    public Elem2<P> self() {
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
        return "elem2";
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }
}
