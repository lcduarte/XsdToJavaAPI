package Samples;

import java.util.List;

public interface IElement<T extends IElement> {
    void addChild(IElement elem);
    void addAttr(IAttribute a);
    T self();
    String id();

    List<IElement<T>> getChildren();
    List<IAttribute> getAttributes();
    void acceptInit(Visitor visitor);
    void acceptEnd(Visitor visitor);
}