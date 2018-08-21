package Samples.HTML;

public interface FlowContent {
    public static <T extends AbstractElement> H1<T> h1() {
        AbstractElement.visitor.visitElement("h1");
        return null;
    }
}