package Samples.HTML;

public interface IFlowContent<T extends IElement> extends IElement<T> {
    default public H1 h1() { H1 h1 = new H1(this); addChild(h1); return h1; }
}
