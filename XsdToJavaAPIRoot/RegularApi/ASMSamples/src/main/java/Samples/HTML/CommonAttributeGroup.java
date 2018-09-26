package samples.html;

public interface CommonAttributeGroup<T extends Element<T, P>, P extends Element> extends Element<T, P>, FlowContent<T, P> {

    default T addAttrClass(String var1) {
        return addAttr(new SomeAttribute(null));
    }

}
