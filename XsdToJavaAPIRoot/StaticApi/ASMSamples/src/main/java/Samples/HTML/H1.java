package Samples.HTML;

public final class H1<P extends AbstractElement> extends AbstractElement implements FlowContent {

    public static <P extends AbstractElement> P º() {
        visitor.visitParent("h1");
        return null;
    }

}
