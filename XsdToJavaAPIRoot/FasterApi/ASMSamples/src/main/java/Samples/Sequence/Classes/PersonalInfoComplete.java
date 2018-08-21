package Samples.Sequence.Classes;

import Samples.HTML.Element;
import Samples.HTML.Visitor;

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
