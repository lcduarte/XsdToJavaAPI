package Samples.HTML;

public interface IFlowContent<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {
    default public H1<T> h1() {
        H1<T> h1 = new H1<>(this.self());
        addChild(h1);
        return h1;
    }
}