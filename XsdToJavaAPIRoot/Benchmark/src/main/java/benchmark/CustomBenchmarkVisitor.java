package benchmark;

import org.xmlet.htmlapi.Element;
import org.xmlet.htmlapi.ElementVisitor;
import org.xmlet.htmlapi.Text;

public class CustomBenchmarkVisitor<R> implements ElementVisitor<R> {

    private StringBuilder stringBuilder = new StringBuilder();

    public <T extends Element> void sharedVisit(Element<T, ?> element) {
        String elementName = element.getName();

        stringBuilder.append("<").append(elementName);

        element.getAttributes().forEach(attribute ->
            stringBuilder.append(" ").append(attribute.getName()).append("=").append(attribute.getValue())
        );

        stringBuilder.append(">\n");

        element.getChildren().forEach(item -> item.accept(this));

        stringBuilder.append("</").append(elementName).append("\n");
    }

    @Override
    public <U> void visit(Text<R, U, ?> text) {
        String textValue = text.getValue();

        if (textValue != null) {
            stringBuilder.append(textValue).append("\n");
        }
    }

    String getResult() {
        return stringBuilder.toString();
    }
}
