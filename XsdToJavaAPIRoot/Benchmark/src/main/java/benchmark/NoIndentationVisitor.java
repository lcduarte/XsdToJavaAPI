package benchmark;

import org.xmlet.htmlapi.Element;
import org.xmlet.htmlapi.ElementVisitor;
import org.xmlet.htmlapi.Text;
import org.xmlet.htmlapi.TextFunction;

public class NoIndentationVisitor<R> extends ElementVisitor<R> {

    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public  <T extends Element> void sharedVisit(Element<T, ?> element) {
        String elementName = element.getName();

        stringBuilder.append('<').append(elementName);

        element.getAttributes().forEach(attribute ->
                stringBuilder.append(' ').append(attribute.getName()).append("=\"").append(attribute.getValue()).append('\"')
        );

        stringBuilder.append('>');

        element.getChildren().forEach(item -> item.accept(this));

        stringBuilder.append("</").append(elementName).append('>');
    }

    @Override
    public void visit(Text text) {
        String textValue = text.getValue();

        if (textValue != null) {
            stringBuilder.append(textValue);
        }
    }

    @Override
    public <U> void visit(TextFunction<R, U, ?> text) {
        /*
        String textValue = text.getValue();

        if (textValue != null) {
            stringBuilder.append(textValue).append("\n");
        }
        */
    }

    String getResult() {
        return stringBuilder.toString();
    }
}
