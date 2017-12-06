package ASMSamples.PreCreationNeeded;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractElement<T extends Element> implements Element<T> {
    protected List<AbstractElement> children = new ArrayList<>();
    protected List<Attribute> attrs = new ArrayList<>();
    protected String id;

    public void addChild(AbstractElement child) {
        this.children.add(child);
    }

    public void addAttr(Attribute attribute) {
        this.attrs.add(attribute);
    }

    @Override
    public String id() {
        return id;
    }

    public <R extends Element> R child(String id) {
        return children.stream()
                .filter(child -> child.id().equals(id))
                .map(child -> (R) child)
                .findFirst()
                .orElse(null);
    } // Enables div.<h1>child("id do h1").....
}