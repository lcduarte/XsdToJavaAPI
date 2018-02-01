package Samples.HTML;

import java.util.List;
import java.util.function.BiConsumer;

public interface IElement<T extends IElement> {
    void addChild(IElement elem);
    void addAttr(IAttribute a);
    T self();
    String id();

    List<IElement<T>> getChildren();
    List<IAttribute> getAttributes();
    <P extends IElement> P getParent();
    String getName();
    void accept(Visitor visitor);

    <M> T binder(BiConsumer<T, M> consumer);
    boolean isBound();
    T cloneElem();
    IElement<T> bindTo(Object model);

}