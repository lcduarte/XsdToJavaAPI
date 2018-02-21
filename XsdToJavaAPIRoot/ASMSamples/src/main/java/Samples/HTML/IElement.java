package Samples.HTML;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface IElement<T extends IElement, P extends IElement> {
    T addChild(IElement elem);
    T addAttr(IAttribute a);
    T self();

    List<IElement> getChildren();
    List<IAttribute> getAttributes();
    <R extends IElement> Stream<R> find(Predicate<IElement> predicate);
    P º();
    String getName();
    void accept(Visitor visitor);

    <M> T binder(BiConsumer<T, M> consumer);
    boolean isBound();
    T cloneElem();
    T bindTo(Object model);

}