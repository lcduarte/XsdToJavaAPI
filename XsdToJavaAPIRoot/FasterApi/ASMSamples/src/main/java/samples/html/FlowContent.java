package samples.html;

public interface FlowContent<T extends Element<T, P>, P extends Element> extends TextGroup<T, P> {
    default public H1<T> h1() {
        return new H1<>(self());
    }

    // Maybe add this?
    default public <O extends Element<O, T>> O addCustomElem(O elem){
        return elem;
    }
}