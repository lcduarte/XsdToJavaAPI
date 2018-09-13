package Samples.Sequence.Classes;

import Samples.HTML.Element;
import Samples.HTML.TextGroup;
import Samples.HTML.Visitor;

public class AName<P extends Element> implements TextGroup<AName<P>, P> {

    protected final P parent;
    protected final Visitor visitor;

    public AName(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
        //visitor.visitElement("personInfo");
    }

    public AName(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
        //visitor.visitElement("personInfo");
    }

    protected AName(P parent, Visitor visitor, boolean visits) {
        this.parent = parent;
        this.visitor = visitor;

        if(visits){
            visitor.visitElement(this);
        }
    }

    @Override
    public AName<P> self() {
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

    public ANameElem1<P> elem1(String value){
        new Elem1<>(visitor, this).text(value).º();
        return new ANameElem1<>(parent);
    }
}