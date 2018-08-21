package Samples.Sequence.Classes;

import Samples.HTML.Element;
import Samples.HTML.Visitor;

public class PersonalInfoFirstName<P extends Element> implements Element<PersonalInfoFirstName<P>, P> {
    protected final P parent;
    protected final Visitor visitor;

    public PersonalInfoFirstName(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
    }

    public PersonalInfoFirstName(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
    }

    @Override
    public PersonalInfoFirstName<P> self() {
        return this;
    }

    @Override
    public P º() {
        return parent;
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
    public String getName() {
        return "personInfo";
    }

    public PersonalInfoLastName<P> lastName(String value){
        visitor.visitElement("lastName");
        visitor.visitText(value);
        visitor.visitParent("lastName");
        return new PersonalInfoLastName<>(parent);
    }

}
