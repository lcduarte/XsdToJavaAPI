package Samples;

import java.util.List;
import java.util.function.BiConsumer;

public interface IElement<T extends IElement, M> {
    void addChild(IElement elem);
    void addAttr(IAttribute a);
    T self();
    String id();

    List<IElement<T, ?>> getChildren();
    List<IAttribute> getAttributes();
    <P extends IElement> P getParent();
    String getName();
    void accept(Visitor visitor);

    void binder(BiConsumer<IElement, M> consumer);
    boolean isBound();
    IElement cloneElem();
    void bindTo(M model);
}