package Samples.HTML;

public interface Element<T extends Element, P extends Element> {

    T self();
    P º();
    P getParent();

    String getName();
    Visitor getVisitor();

}