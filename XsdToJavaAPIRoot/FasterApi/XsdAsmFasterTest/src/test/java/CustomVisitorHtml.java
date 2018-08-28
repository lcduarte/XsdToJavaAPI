import org.xmlet.htmlFaster.ElementVisitor;

@SuppressWarnings("Duplicates")
public class CustomVisitorHtml extends ElementVisitor {

    private int tabCount = 0;
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void visitElement(String elementName) {
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

        doTabs();
        stringBuilder.append('<').append(elementName);
        ++tabCount;
    }

    private void doTabs() {
        for (int i = 0; i < tabCount; i++) {
            stringBuilder.append("\t");
        }
    }

    @Override
    public void visitAttribute(String attributeName, String attributeValue) {
        stringBuilder.append(' ').append(attributeName).append("=\"").append(attributeValue).append('\"');
    }

    @Override
    public void visitParent(String elementName) {
        char lastChar = stringBuilder.charAt(stringBuilder.length() - 1);

        if (lastChar != '\n' && lastChar != '>'){
            stringBuilder.append('>');
        }

        if (lastChar != '\n'){
            stringBuilder.append('\n');
        }

        --tabCount;
        doTabs();
        stringBuilder.append("</").append(elementName).append('>');
    }

    String getResult(){
        return stringBuilder.toString();
    }

    @Override
    public <R> void visitText(R text){
        if (text != null){
            stringBuilder.append(">\n");
            doTabs();
            stringBuilder.append(text).append('\n');
        }
    }

    @Override
    public <R> void visitComment(R comment){
        if (comment != null){
            stringBuilder.append(">\n");
            doTabs();
            stringBuilder.append("<!-- ").append(comment).append(" -->\n");
        }
    }
}
