package org.xmlet.htmlapitest.utils;

import org.xmlet.htmlapifaster.*;

public class CustomVisitor extends ElementVisitor {

    private int tabCount = 0;
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void visit(Element elem) {
        int length = stringBuilder.length();
        boolean isClosed = stringBuilder.lastIndexOf(">") == length - 1;
        boolean isNewlined = stringBuilder.lastIndexOf("\n") == length - 1;
        boolean shouldClose = length != 0 && !isClosed && !isNewlined;

        if(shouldClose) {
            stringBuilder.append(">\n");
        }

        if (length != 0 && isClosed && !isNewlined){
            stringBuilder.append("\n");
        }

        doTabs();
        stringBuilder.append("<").append(elem.getName());
        ++tabCount;
    }

    private void doTabs() {
        for (int i = 0; i < tabCount; i++) {
            stringBuilder.append("\t");
        }
    }

    @Override
    public void visit(Attribute attribute) {
        stringBuilder.append(" ").append(attribute.getName()).append("=\"").append(attribute.getValue()).append("\"");
    }

    @Override
    public void visitParent(Element element) {
        closeIfNeeded();

        if (!stringBuilder.toString().endsWith("\n")){
            stringBuilder.append("\n");
        }

        --tabCount;
        doTabs();
        stringBuilder.append("</").append(element.getName()).append(">");

    }

    private void closeIfNeeded(){
        if (!stringBuilder.toString().endsWith("\n") && !stringBuilder.toString().endsWith(">")){
            stringBuilder.append(">");
        }
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
            stringBuilder.append(">\n");
            doTabs();
            stringBuilder.append(textValue).append("\n");
        }
    }

    @Override
    public void visit(Comment comment){
        String textValue = comment.getValue();

        if (textValue != null){
            stringBuilder.append(">\n");
            doTabs();
            stringBuilder.append("<!-- ").append(textValue).append(" -->\n");
        }
    }
}
