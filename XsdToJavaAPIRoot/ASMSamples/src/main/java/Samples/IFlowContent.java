package Samples;

public interface IFlowContent<T extends IElement> extends IElement<T> {
    default public H1 h1() { H1 h1 = new H1(); addChild(h1); return h1; }
    default public T h1(String id) { H1 h1 = new H1(id); addChild(h1); return this.self(); }
    default public T h1(String id, String text) { H1 h1 = new H1(id, text); addChild(h1); return this.self(); }
}
