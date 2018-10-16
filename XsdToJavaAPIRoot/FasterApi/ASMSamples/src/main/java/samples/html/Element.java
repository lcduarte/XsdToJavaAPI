package samples.html;

public interface Element<T extends Element, P extends Element> {

    T self();
    P __();
    P getParent();

    String getName();
    Visitor getVisitor();

}