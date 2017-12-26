package Samples;

public interface IElement<T extends IElement> {
    void addChild(IElement elem);
    void addAttr(IAttribute a);
    T self();
    String id();

    void acceptInit(Visitor visitor);
    void acceptEnd(Visitor visitor);
}