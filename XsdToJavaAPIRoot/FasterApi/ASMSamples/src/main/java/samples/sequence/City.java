package samples.sequence;

import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class City<P extends Element> implements TextGroup<City<P>, P> {

    private Visitor visitor;
    private P parent;

    public City(Visitor visitor, P parent) {
        this.visitor = visitor;
        this.parent = parent;
        //regex.visitor.visitElementFirstName(this);
    }

    @Override
    public City<P> self() {
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
        return "city";
    }

    @Override
    public Visitor getVisitor() {
        return visitor;
    }
}
