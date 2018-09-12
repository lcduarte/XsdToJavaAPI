import org.xmlet.androidFaster.Element;
import org.xmlet.androidFaster.ElementVisitor;
import org.xmlet.androidFaster.Text;

@SuppressWarnings("Duplicates")
public class CustomVisitorAndroid extends ElementVisitor {

    private int tabCount = 0;
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void visitElement(Element element) {
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
        stringBuilder.append('<').append(element.getName());
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
    public void visitParent(Element element) {
        char lastChar = stringBuilder.charAt(stringBuilder.length() - 1);

        if (lastChar != '\n' && lastChar != '>'){
            stringBuilder.append('>');
        }

        if (lastChar != '\n'){
            stringBuilder.append('\n');
        }

        --tabCount;
        doTabs();
        stringBuilder.append("</").append(element.getName()).append('>');
    }

    String getResult(){
        return stringBuilder.toString();
    }

    @Override
    public <R> void visitText(Text<? extends Element, R> text){
        if (text != null){
            stringBuilder.append(">\n");
            doTabs();
            stringBuilder.append(text.getValue()).append('\n');
        }
    }

    @Override
    public <R> void visitComment(Text<? extends Element, R> comment){
        if (comment != null){
            stringBuilder.append(">\n");
            doTabs();
            stringBuilder.append("<!-- ").append(comment.getValue()).append(" -->\n");
        }
    }
}
