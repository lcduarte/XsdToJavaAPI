package Samples.HTML;

public interface TextGroup {

    default <T extends AbstractElement> T text(String text){
        AbstractElement.visitor.visitText(text);
        return null;
    }

    default <T extends AbstractElement> T comment(String text){
        AbstractElement.visitor.visitComment(text);
        return null;
    }

}
