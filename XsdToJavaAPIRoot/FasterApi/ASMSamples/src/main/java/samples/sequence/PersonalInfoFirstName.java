package samples.sequence;

import samples.html.Element;
import samples.html.Visitor;

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

    public PersonalInfoFirstName(P parent, Visitor visitor) {
        this.parent = parent;
        this.visitor = visitor;
    }

    @Override
    public PersonalInfoFirstName<P> self() {
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

    public PersonalInfoLastName<P> lastName(String value){
        new LastName<>(visitor, this).text(value).__();
        return new PersonalInfoLastName<>(parent, visitor);
    }

}
