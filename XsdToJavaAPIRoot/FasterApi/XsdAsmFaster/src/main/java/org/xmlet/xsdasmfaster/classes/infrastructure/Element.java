package org.xmlet.xsdasmfaster.classes.infrastructure;

public interface Element<T extends Element, P extends Element> {

    T self();
    P º();
    P getParent();

    String getName();
    ElementVisitor getVisitor();

}