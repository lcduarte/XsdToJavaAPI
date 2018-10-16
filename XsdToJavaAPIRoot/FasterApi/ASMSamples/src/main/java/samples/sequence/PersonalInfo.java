package samples.sequence;

import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class PersonalInfo<P extends Element> implements TextGroup<PersonalInfo<P>, P> {

    protected final P parent;
    protected final Visitor visitor;

    public PersonalInfo(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
        //regex.visitor.visitElement("personInfo");
    }

    public PersonalInfo(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
        //regex.visitor.visitElement("personInfo");
    }

    public PersonalInfo(P parent, Visitor visitor) {
        this.parent = parent;
        this.visitor = visitor;
        //regex.visitor.visitElement("personInfo");
    }

    @Override
    public PersonalInfo<P> self() {
        return this;
    }

    @Override
    public P __() {
        //regex.visitor.visitParent("personInfo");
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

    public PersonalInfoFirstName<P> firstName(Integer value){
        new FirstName<>( this, visitor).text(value).__();
        return new PersonalInfoFirstName<>(parent, visitor);
    }
}
