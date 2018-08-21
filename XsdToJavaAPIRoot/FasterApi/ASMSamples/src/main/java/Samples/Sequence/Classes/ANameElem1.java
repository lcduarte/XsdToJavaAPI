package Samples.Sequence.Classes;

import Samples.HTML.Element;
import Samples.HTML.Visitor;

public class ANameElem1<P extends Element> implements Element<ANameElem1<P>, P> {

    protected final P parent;
    protected final Visitor visitor;

    public ANameElem1(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
        visitor.visitElement("personInfo");
    }

    public ANameElem1(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
        visitor.visitElement("personInfo");
    }

    @Override
    public ANameElem1<P> self() {
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

    public AName<P> elem2(String value){
        visitor.visitElement("elem2");
        visitor.visitText(value);
        visitor.visitParent("elem2");
        return new AName<>(parent, visitor);
    }
}