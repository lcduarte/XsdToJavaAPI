package samples.sequence;

import samples.html.Element;
import samples.html.TextGroup;
import samples.html.Visitor;

public class AName<P extends Element> implements TextGroup<AName<P>, P> {

    protected final P parent;
    protected final Visitor visitor;

    public AName(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
        //regex.visitor.visitElement("personInfo");
    }

    public AName(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
        //regex.visitor.visitElement("personInfo");
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

    public ANameElem1<P> elem1(String value){
        new Elem1<>(visitor, this).text(value).__();
        return new ANameElem1<>(parent);
    }
}