package ASMSamples.PreCreationNeeded;

import java.util.List;

public abstract class AbstractElement<T extends Element> implements Element<T> {
    List<AbstractElement> children;
    List<Attribute> attrs;
    public void addChild(AbstractElement elem) {}
    public void addAttr(Attribute a) {}
    public <R extends Element> R child(String id) {return null; } // Enables div.<h1>child("id do h1").....
}