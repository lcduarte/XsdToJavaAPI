package benchmark;

import org.xmlet.htmlapi.*;

public class NoIndentationVisitor<R> extends ElementVisitor<R> {

    private StringBuilder stringBuilder = new StringBuilder();
    private boolean lastWasText = false;

    @Override
    public void visit(Element elem) {
        int length = stringBuilder.length() - 1;

        if (length != -1){
            char lastChar = stringBuilder.charAt(length);
            boolean isClosed = lastChar == '>';

            if (!isClosed && !lastWasText){
                stringBuilder.append(">");
            }
        }

        lastWasText = false;
        stringBuilder.append('<').append(elem.getName());
    }

    @Override
    public void visit(Attribute attribute) {
        stringBuilder.append(' ').append(attribute.getName()).append("=\"").append(attribute.getValue()).append('\"');
    }

    @Override
    public void visitParent(Element element) {
        char lastChar = stringBuilder.charAt(stringBuilder.length() - 1);

        if (lastChar != '>' && !lastWasText){
            stringBuilder.append('>');
            lastWasText = false;
        }

        stringBuilder.append("</").append(element.getName()).append('>');
    }

    public String getResult(Element x){
        while (!(x instanceof Html) ){
            x = x.º();
        }

        x.º();

        return stringBuilder.toString();
    }

    @Override
    public void visit(Text text){
        String textValue = text.getValue();

        if (textValue != null){
            stringBuilder.append('>').append(textValue);
            lastWasText = true;
        }
    }

    @Override
    public <U> void visit(TextFunction<R, U, ?> text){
        /*
        if (model != null){
            U textValue = text.getValue(model);
            stringBuilder.append(">\n");
            doTabs();
            stringBuilder.append(textValue).append("\n");
        }
        */
    }
}
