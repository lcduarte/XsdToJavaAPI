package Samples.HTML;

public interface CustomAttributeGroup<T extends Element, P extends Element> extends Element<T, P> {

    default T addCustomAttr(String name, String value) {
        getVisitor().visitAttribute(name, value);
        return this.self();
    }

}

