package Samples.HTML;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class AbstractElement<T extends Element<T, P>, P extends Element> implements Element<T, P> {
    protected List<Element> children = new ArrayList<>();
    protected List<IAttribute> attrs = new ArrayList<>();
    protected String name;
    protected P parent;
    protected BiConsumer binderMethod;

    protected AbstractElement(){
        setName();
    }

    protected AbstractElement(String name){
        this.name = name;
    }

    protected AbstractElement(P parent){
        this.parent = parent;

        setName();
    }

    protected AbstractElement(P parent, String name){
        this.parent = parent;
        this.name = name;
    }

    private void setName() {
        String simpleName = getClass().getSimpleName();

        this.name = simpleName.toLowerCase().charAt(0) + simpleName.substring(1);
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

    @Override
    public P º() {
        return parent;
    }

    public <R extends Element> Stream<R> find(Predicate<Element> predicate){
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

    public List<Element> getChildren() {
        return children;
    }

    public List<IAttribute> getAttributes() {
        return attrs;
    }

    public String getName(){
        return name;
    }

    @Override
    public <M> T binder(BiConsumer<T, M> consumer) {
        this.binderMethod = consumer;
        return this.self();
    }

    @Override
    public boolean isBound() {
        return binderMethod != null;
    }

    @Override
    public T bindTo(Object model) {
        if (isBound()){
            binderMethod.accept(this.self(), model);
        }

        return this.self();
    }

    protected <X extends AbstractElement> X clone(X clone) {
        clone.children = new ArrayList();
        clone.children.addAll(this.children);

        clone.attrs = new ArrayList();
        clone.attrs.addAll(this.attrs);

        clone.name = this.name;
        clone.parent = this.parent;
        clone.binderMethod = this.binderMethod;

        return clone;
    }
}