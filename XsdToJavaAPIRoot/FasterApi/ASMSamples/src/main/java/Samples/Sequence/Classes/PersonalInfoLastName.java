package Samples.Sequence.Classes;

import Samples.HTML.Element;
import Samples.HTML.Visitor;

public class PersonalInfoLastName<P extends Element> implements Element<PersonalInfoLastName<P>, P> {

    protected final P parent;
    protected final Visitor visitor;

    public PersonalInfoLastName(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
        visitor.visitElement(this);
    }

    public PersonalInfoLastName(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
        visitor.visitElement(this);
    }

    @Override
    public PersonalInfoLastName<P> self() {
        return this;
    }

    @Override
    public P º() {
        visitor.visitParent(this);
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

/*
    public PersonalInfoAddress<P> address(String value){
        visitor.visitElement("address");
        visitor.visitText(value);
        visitor.visitParent("address");
        return new PersonalInfoAddress<>(parent);
    }*/
}

