package ASMSamples.PreCreationNeeded;

public interface Element<T extends Element> {
    void addChild(AbstractElement elem);
    void addAttr(Attribute a);
    T self();
}