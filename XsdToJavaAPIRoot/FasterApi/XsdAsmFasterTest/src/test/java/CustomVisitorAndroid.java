import org.xmlet.android.*;

@SuppressWarnings("Duplicates")
public class CustomVisitorAndroid extends ElementVisitor {

    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void visit(Element elem) {
        int length = stringBuilder.length() - 1;

        if (length != -1){
            char lastChar = stringBuilder.charAt(length);
            boolean isClosed = lastChar == '>';
            boolean isNewlined = lastChar == '\n';

            if (!isNewlined){
                if (!isClosed){
                    stringBuilder.append(">\n");
                } else {
                    stringBuilder.append('\n');
                }
            }
        }

        doTabs(elem.getDepth());
        stringBuilder.append('<').append(elem.getName());
    }

    private void doTabs(int tabCount) {
        char[] tabs = new char[tabCount];

        for (int i = 0; i < tabCount; i++) {
            tabs[i] = '\t';
        }

        stringBuilder.append(tabs);
    }

    @Override
    public void visit(Attribute attribute) {
        stringBuilder.append(' ').append(attribute.getName()).append("=\"").append(attribute.getValue()).append('\"');
    }

    @Override
    public void visitParent(Element element) {
        char lastChar = stringBuilder.charAt(stringBuilder.length() - 1);

        if (lastChar != '\n' && lastChar != '>'){
            stringBuilder.append('>');
        }

        if (lastChar != '\n'){
            stringBuilder.append('\n');
        }

        doTabs(element.getDepth());
        stringBuilder.append("</").append(element.getName()).append('>');
    }

    String getResult(Element x){
        while (!(x instanceof Element) ){
            x = x.º();
        }

        x.º();

        return stringBuilder.toString();
    }

    @Override
    public void visit(Text text){
        String textValue = text.getValue();

        if (textValue != null){
            stringBuilder.append(">\n");
            doTabs(text.getDepth());
            stringBuilder.append(textValue).append('\n');
        }
    }

    @Override
    public void visit(Comment comment){
        String textValue = comment.getValue();

        if (textValue != null){
            stringBuilder.append(">\n");
            doTabs(comment.getDepth());
            stringBuilder.append("<!-- ").append(textValue).append(" -->\n");
        }
    }
}
