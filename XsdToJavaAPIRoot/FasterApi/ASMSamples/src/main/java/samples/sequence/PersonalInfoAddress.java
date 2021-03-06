package samples.sequence;

import samples.html.Element;
import samples.html.Visitor;

public class PersonalInfoAddress<P extends Element> implements Element<PersonalInfoAddress<P>, P>{

    protected final P parent;
    protected final Visitor visitor;

    public PersonalInfoAddress(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
    }

    public PersonalInfoAddress(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
    }

    public PersonalInfoAddress(P parent, Visitor visitor) {
        this.parent = parent;
        this.visitor = visitor;
    }

    @Override
    public PersonalInfoAddress<P> self() {
        return this;
    }

    @Override
    public P __() {
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

    public PersonalInfoCity<P> city(String value){
        new Address<>(visitor, this).text(value).__();
        return new PersonalInfoCity<>(parent, visitor);
    }

}
