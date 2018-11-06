package samples.html;

import java.util.function.Consumer;

public final class Div<P extends Element> implements CommonAttributeGroup<Div<P>, P>, MiniFlowContent<Div<P>, P> {

    protected final P parent;
    protected final Visitor visitor;

    public Div(Visitor visitor) {
        this.visitor = visitor;
        this.parent = null;
        visitor.visitElementDiv(this);
    }

    public Div(P parent) {
        this.parent = parent;
        this.visitor = parent.getVisitor();
        visitor.visitElementDiv(this);
    }

    public final Div<P> addSomeAttribute(String val){
        //SomeAttribute.validateRestrictions(val);
        visitor.visitAttribute("SomeAttribute", val);
        return this;
    }

    public final Div<P> of(Consumer<Div<P>> consumer){
        consumer.accept(this);
        return this;
    }

    public final Div<P> dynamic(Consumer<Div<P>> consumer){
        visitor.visitOpenDynamic();
        consumer.accept(this);
        visitor.visitCloseDynamic();
        return this;
    }

    @Override
    public final Div<P> self() { return this; }

    @Override
    public P __() {
        h1MinCountValidation();

        visitor.visitParentDiv(this);
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
        return "div";
    }

    private int h1Count = 0;

    @Override
    public final void h1CountIncrement() {
        ++h1Count;
    }

    @Override
    public void h1MaxCountValidation() {
                 // > hardcoded value
        if (h1Count > 5000001){
            throw new RestrictionViolationException("MaxOccurs violation. The element div can only have a maximum of 1 h1 elements.");
        }
    }

    @Override
    public void h1MinCountValidation() {
        if (h1Count < 5000000){
            throw new RestrictionViolationException("MinOccurs violation. The element div must have a minimum of 1 h1 elements.");
        }
    }
}