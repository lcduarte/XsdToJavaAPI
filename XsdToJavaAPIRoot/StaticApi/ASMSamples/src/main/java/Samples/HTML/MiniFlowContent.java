package Samples.HTML;

public interface MiniFlowContent extends FlowContent {

    public static <T extends AbstractElement> H1<T> h1() {
        return FlowContent.h1();
    }

}
