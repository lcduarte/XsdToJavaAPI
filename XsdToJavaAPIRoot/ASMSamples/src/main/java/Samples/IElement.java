package Samples;

import java.util.List;
import java.util.function.Consumer;

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

    void binder(Consumer<T> consumer);
    boolean isBound();
    IElement<T> cloneElem();
    IElement<T> binderApply();

}