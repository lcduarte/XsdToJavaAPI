package Samples;

public interface IFlowContent<T extends IElement, M> extends IElement<T, M> {
    default public H1 h1() { H1 h1 = new H1(this); addChild(h1); return h1; }
    default public T h1(String id) { H1 h1 = new H1(this, id); addChild(h1); return this.self(); }
}
