package Samples.Sequence.Classes;

import Samples.HTML.Element;
import Samples.HTML.Visitor;

public class PersonalInfoCity<P extends Element> implements Element<PersonalInfoCity<P>, P> {

    protected final P parent;
    protected final Visitor visitor;

    public PersonalInfoCity(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
        //visitor.visitElement("personInfo");
    }

    public PersonalInfoCity(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
        //visitor.visitElement("personInfo");
    }

    public PersonalInfoCity(P parent, Visitor visitor) {
        this.parent = parent;
        this.visitor = visitor;
    }

    @Override
    public PersonalInfoCity<P> self() {
        return this;
    }

    @Override
    public P º() {
        //visitor.visitParent("personInfo");
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

    public PersonalInfoComplete<P> country(String value){
        new Country<>(visitor, this).text(value).º();
        return new PersonalInfoComplete<>(parent, visitor);
    }
}

