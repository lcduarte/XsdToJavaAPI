package Samples;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractElement<T extends IElement> implements IElement<T> {
    protected List<IElement> children = new ArrayList<>();
    protected List<IAttribute> attrs = new ArrayList<>();
    protected String id;

    public void addChild(IElement child) {
        this.children.add(child);
    }

    public void addAttr(IAttribute attribute) {
        this.attrs.add(attribute);
    }

    @Override
    public String id() {
        return id;
    }

    public <R extends IElement> R child(String id) {
        return children.stream()
                .filter(child -> child.id().equals(id))
                .map(child -> (R) child)
                .findFirst()
                .orElse(null);
    } // Enables div.<h1>child("id do h1").....
}