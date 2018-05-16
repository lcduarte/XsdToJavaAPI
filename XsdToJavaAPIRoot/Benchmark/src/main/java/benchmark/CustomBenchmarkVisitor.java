package benchmark;

import org.xmlet.htmlapi.Element;
import org.xmlet.htmlapi.ElementVisitor;
import org.xmlet.htmlapi.Text;

import java.util.List;
import javax.validation.constraints.NotNull;

public class CustomBenchmarkVisitor<R> implements ElementVisitor<R> {

    private R model;
    private StringBuilder stringBuilder = new StringBuilder();

    public CustomBenchmarkVisitor(@NotNull R model) {
        this.model = model;
    }

    public <T extends Element> void sharedVisit(Element<T, ?> element) {
        String elementName = element.getName();

        stringBuilder.append("<").append(elementName);

        element.getAttributes().forEach(attribute -> stringBuilder.append(" ").append(attribute.getName()).append("=").append(attribute.getValue()));

        stringBuilder.append(">\n");

        if (element.isBound()) {
            List<Element> children = element.cloneElem().bindTo(model).getChildren();
            children.forEach(child -> child.accept(this));
        } else {
            element.getChildren().forEach(item -> item.accept(this));
        }

        stringBuilder.append("</").append(elementName).append("\n");
    }

    @Override
    public <U> void visit(Text<R, U, ?> text) {
        String textValue = text.getValue();

        if (textValue != null) {
            stringBuilder.append(textValue).append("\n");
        } else {
            stringBuilder.append(text.getValue(model)).append("\n");
        }
    }

    public String getResult() {
        return stringBuilder.toString();
    }
}
