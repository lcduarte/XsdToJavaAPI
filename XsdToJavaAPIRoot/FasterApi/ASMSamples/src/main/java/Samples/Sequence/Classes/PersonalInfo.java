package Samples.Sequence.Classes;

import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class PersonalInfo<P extends Element> implements TextGroup<PersonalInfo<P>, P> {

    protected final P parent;
    protected final Visitor visitor;

    public PersonalInfo(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
        visitor.visitElement("personInfo");
    }

    public PersonalInfo(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
        visitor.visitElement("personInfo");
    }

    @Override
    public PersonalInfo<P> self() {
        return this;
    }

    @Override
    public P º() {
        visitor.visitParent("personInfo");
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

    public PersonalInfoFirstName<P> firstName(String value){
        visitor.visitElement("firstName");
        visitor.visitText(value);
        visitor.visitParent("firstName");
        return new PersonalInfoFirstName<>(parent);
    }
}
