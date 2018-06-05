package Samples.HTML;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Element<T extends Element, P extends Element> {
    <R extends Element> R addChild(R elem);
    T addAttr(IAttribute a);
    T self();

    List<Element> getChildren();
    List<IAttribute> getAttributes();

    <R extends Element> Stream<R> find(Predicate<Element> predicate);
    P º();
    String getName();
    void accept(Visitor visitor);

    <M> T binder(BiConsumer<T, M> consumer);
    boolean isBound();
    T cloneElem();
    T bindTo(Object model);

}