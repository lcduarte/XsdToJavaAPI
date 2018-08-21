package Samples.HTML;

public interface CommonAttributeGroup<T extends Element<T, P>, P extends Element> extends FlowContent<T, P> {

    default T addAttrClass(Object var1) {
        //SomeAttribute.validateRestrictions((String)var1);
        getVisitor().visitAttribute("SomeAttribute", var1.toString());
        return this.self();
    }

}
