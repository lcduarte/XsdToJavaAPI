package Samples.HTML;

public interface CommonAttributeGroup<T extends Element<T, P>, P extends Element> extends Element<T, P>, FlowContent<T, P> {

    default T addAttrClass(String var1) {
        getVisitor().visit(new SomeAttribute(null));
        return this.self();
    }

}
