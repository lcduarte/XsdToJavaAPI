package Samples;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractElement<T extends IElement> implements IElement<T> {
    protected List<IElement<T>> children = new ArrayList<>();
    protected List<IAttribute> attrs = new ArrayList<>();
    protected String id;
    protected String name;
    protected IElement parent;

    protected AbstractElement(){
        this.parent = null;

        setName();
    }

    protected AbstractElement(IElement parent){
        this.parent = parent;

        setName();
    }

    protected AbstractElement(IElement parent, String id){
        this.parent = parent;
        this.id = id;

        setName();
    }

    private void setName() {
        String simpleName = getClass().getSimpleName();

        this.name = simpleName.toLowerCase().charAt(0) + simpleName.substring(1);
    }

    public void addChild(IElement child) {
        this.children.add(child);
    }

    public void addAttr(IAttribute attribute) {
        this.attrs.add(attribute);
    }

    @Override
    public <P extends IElement> P getParent() {
        return (P) parent.self();
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

    public List<IElement<T>> getChildren() {
        return children;
    }

    public List<IAttribute> getAttributes() {
        return attrs;
    }

    public String getName(){
        return name;
    }
}