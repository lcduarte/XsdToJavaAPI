package Samples.HTML;

public interface IFlowContent<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {
    default public H1<T> h1() {
        return addChild(new H1<>(self()));
    }
}