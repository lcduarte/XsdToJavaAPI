package Samples.Sequence.Classes;

import Samples.HTML.Element;
import Samples.HTML.Visitor;

public class PersonalInfoAddress<P extends Element> implements Element<PersonalInfoAddress<P>, P>{

    protected final P parent;
    protected final Visitor visitor;

    public PersonalInfoAddress(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
        visitor.visitElement("personInfo");
    }

    public PersonalInfoAddress(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
        visitor.visitElement("personInfo");
    }

    @Override
    public PersonalInfoAddress<P> self() {
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

    public PersonalInfoCity<P> city(String value){
        visitor.visitElement("city");
        visitor.visitText(value);
        visitor.visitParent("city");
        return new PersonalInfoCity<>(parent);
    }

}
