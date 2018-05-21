package Samples.HTML;

public interface FlowContent<T extends Element<T, P>, P extends Element> extends Element<T, P> {
    default public H1<T> h1() {
        return addChild(new H1<>(self()));
    }
}