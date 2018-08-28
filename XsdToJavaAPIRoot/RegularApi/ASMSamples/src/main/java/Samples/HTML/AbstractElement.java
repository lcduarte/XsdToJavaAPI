package Samples.HTML;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class AbstractElement<T extends Element<T, P>, P extends Element> implements Element<T, P> {
    @SuppressWarnings("WeakerAccess")
    protected List<Element> children = new ArrayList<>();
    @SuppressWarnings("WeakerAccess")
    protected List<IAttribute> attrs = new ArrayList<>();
    @SuppressWarnings("WeakerAccess")
    protected String name;
    protected P parent;
    @SuppressWarnings("WeakerAccess")
    protected BiConsumer binderMethod;

    protected AbstractElement(String name){
        this.name = name;
    }

    protected AbstractElement(P parent, String name){
        this.parent = parent;
        this.name = name;
    }

    @Override
    public <R extends Element> R addChild(R child) {
        this.children.add(child);
        return child;
    }

    @Override
    public T addAttr(IAttribute attribute) {
        this.attrs.add(attribute);
        return this.self();
    }

    @SuppressWarnings("NonAsciiCharacters")
    @Override
    public P º() {
        return parent;
    }

    public final <R extends Element> Stream<R> find(Predicate<Element> predicate){
        Supplier<Stream<R>> resultSupplier = () -> children.stream().filter(predicate).map(child -> (R) child);

        if (resultSupplier.get().count() != 0){
            return resultSupplier.get();
        }

        final Stream[] childResult = {Stream.empty()};

        children.forEach(child ->
            childResult[0] = Stream.concat(childResult[0], child.find(predicate))
        );

        //noinspection unchecked
        return (Stream<R>) childResult[0];
    }

    public final List<Element> getChildren() {
        return children;
    }

    public final List<IAttribute> getAttributes() {
        return attrs;
    }

    public final String getName(){
        return name;
    }

    @Override
    public final <M> T binder(BiConsumer<T, M> consumer) {
        this.binderMethod = consumer;
        return this.self();
    }

    @Override
    public final boolean isBound() {
        return binderMethod != null;
    }

    @Override
    public final Element<T, P> bindTo(Object model) {
        if (isBound()){
            binderMethod.accept(this.self(), model);
        }

        return this.self();
    }

    public final T of(Consumer<T> consumer){
        consumer.accept(self());
        return self();
    }

    final <X extends AbstractElement<T, P>> X clone(X clone) {
        clone.children = new ArrayList<>(this.children);
        clone.attrs = new ArrayList<>(this.attrs);

        clone.name = this.name;
        clone.parent = this.parent;
        clone.binderMethod = this.binderMethod;

        return clone;
    }
}