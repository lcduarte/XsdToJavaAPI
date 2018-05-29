package benchmark;

import org.xmlet.htmlapi.Element;
import org.xmlet.htmlapi.ElementVisitor;
import org.xmlet.htmlapi.Text;
import org.xmlet.htmlapi.TextFunction;

public class CustomBenchmarkVisitor<R> extends ElementVisitor<R> {

    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public  <T extends Element> void visit(Element<T, ?> element) {
        String elementName = element.getName();
        int depth = element.getDepth();

        doTabs(depth);

        stringBuilder.append('<').append(elementName);

        element.getAttributes().forEach(attribute ->
                    stringBuilder.append(' ').append(attribute.getName()).append("=\"").append(attribute.getValue()).append('\"')
        );

        stringBuilder.append(">\n");


        ++depth;
        element.getChildren().forEach(item -> item.accept(this));
        --depth;

        doTabs(depth);
        stringBuilder.append("</").append(elementName).append(">\n");
    }

    private void doTabs(int tabCount) {
        /*
        char[] tabs = new char[tabCount];

        for (int i = 0; i < tabCount; i++) {
            tabs[i] = '\t';
        }

        stringBuilder.append(tabs);
        */

        for (int i = 0; i < tabCount; i++) {
            stringBuilder.append('\t');
        }
    }

    @Override
    public void visit(Text text) {
        String textValue = text.getValue();

        if (textValue != null) {
            doTabs(text.getDepth());
            stringBuilder.append(textValue).append('\n');
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
