package org.xmlet.htmlapitest.utils;

import org.xmlet.htmlapifaster.ElementVisitor;

public class CustomVisitor extends ElementVisitor {

    private int tabCount = 0;
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void visitElement(String elementName) {
        int length = stringBuilder.length();
        boolean isClosed = stringBuilder.lastIndexOf(">") == length - 1;
        boolean isNewlined = stringBuilder.lastIndexOf("\n") == length - 1;
        boolean shouldClose = length != 0 && !isClosed && !isNewlined;

        if(shouldClose) {
            stringBuilder.append(">\n");
        }

        if (length != 0 && isClosed && !isNewlined){
            stringBuilder.append('\n');
        }

        doTabs();
        stringBuilder.append('<').append(elementName);
        ++tabCount;
    }

    private void doTabs() {
        /*
        for (int i = 0; i < tabCount; i++) {
            stringBuilder.append('\t');
        }
        */

        char[] tabs = new char[tabCount];

        for (int i = 0; i < tabCount; i++) {
            tabs[i] = '\t';
        }

        stringBuilder.append(tabs);
    }

    @Override
    public void visitAttribute(String attributeName, String attributeValue) {
        stringBuilder.append(' ').append(attributeName).append("=\"").append(attributeValue).append('\"');
    }

    @Override
    public void visitParent(String elementName) {
        closeIfNeeded();

        if (!stringBuilder.toString().endsWith("\n")){
            stringBuilder.append('\n');
        }

        --tabCount;
        doTabs();
        stringBuilder.append("</").append(elementName).append('>');

    }

    private void closeIfNeeded(){
        if (!stringBuilder.toString().endsWith("\n") && !stringBuilder.toString().endsWith(">")){
            stringBuilder.append('>');
        }
    }

    public String getResult(){
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
