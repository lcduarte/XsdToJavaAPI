package samples.sequence;

import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class LastName<P extends Element> implements TextGroup<LastName<P>, P> {

    private Visitor visitor;
    private P parent;

    public LastName(Visitor visitor, P parent) {
        this.visitor = visitor;
        this.parent = parent;
        //regex.visitor.visitElementFirstName(this);
    }

    @Override
    public LastName<P> self() {
        return this;
    }

    @Override
    public P __() {
        //regex.visitor.visitParentFirstName(this);
        return parent;
    }

    @Override
    public P getParent() {
        return parent;
    }

    @Override
    public String getName() {
        return "lastName";
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }
}
