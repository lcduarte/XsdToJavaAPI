package samples.sequence;

import samples.html.Element;
import samples.html.Visitor;

public class PersonalInfoComplete<P extends Element> implements Element<PersonalInfoComplete<P>, P> {

    protected final P parent;
    protected final Visitor visitor;

    public PersonalInfoComplete(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
    }

    public PersonalInfoComplete(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
    }

    public PersonalInfoComplete(P parent, Visitor visitor) {
        this.parent = parent;
        this.visitor = visitor;
    }

    @Override
    public PersonalInfoComplete<P> self() {
        return this;
    }

    @Override
    public P getParent() {
        return parent;
    }

    @Override
    public final Visitor getVisitor() {
        return visitor;
    }

    @Override
    public P º() {
        return parent;
    }

    @Override
    public String getName() {
        return "personInfo";
    }
}
