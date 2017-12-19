package Samples;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Optional<R> elem = children.stream()
                .filter(child -> child.id() != null && child.id().equals(id))
                .map(child -> (R) child)
                .findFirst();

        if (elem.isPresent()){
            return elem.get();
        }

        return children.stream()
                .filter(iElement -> iElement instanceof AbstractElement)
                .map(iElement -> (AbstractElement) iElement)
                .filter(element -> (R) element.child(id) != null)
                .map(element -> (R) element.child(id))
                .findFirst()
                .orElse(null);
    }
}