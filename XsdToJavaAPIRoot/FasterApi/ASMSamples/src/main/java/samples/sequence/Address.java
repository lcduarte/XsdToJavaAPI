package samples.sequence;

import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class Address<P extends Element> implements TextGroup<Address<P>, P> {

    private Visitor visitor;
    private P parent;

    public Address(Visitor visitor, P parent) {
        this.visitor = visitor;
        this.parent = parent;
        //regex.visitor.visitElementFirstName(this);
    }

    @Override
    public Address<P> self() {
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
        return "address";
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }
}
