package Samples.HTML;

public abstract class AbstractElement<T extends AbstractElement<T, P>, P extends AbstractElement> {

    public static Visitor visitor;

    public static Visitor getVisitor() {
        return visitor;
    }

}