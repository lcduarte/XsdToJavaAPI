package com.jeroenreijn.examples.htmlApiFaster;

import org.xmlet.htmlapifaster.*;

import java.io.PrintWriter;

public class FasterCustomVisitor extends ElementVisitor<java.lang.Object> {

    private PrintWriter printWriter;
    private boolean empty = true;
    private boolean isNewlined = false;
    private boolean isClosed = false;

    public FasterCustomVisitor(PrintWriter printWriter){
        this.printWriter = printWriter;
    }

    @Override
    public void visit(Element elem) {
        if (!empty && !isNewlined){
            if (!isClosed){
                print(">\n");
            } else {
                print("\n");
            }
        }

        //doTabs(elem.getDepth());
        print('<' + elem.getName());
        isClosed = false;
        isNewlined = false;
        empty = false;
    }

    private void doTabs(int tabCount) {
        char[] tabs = new char[tabCount];

        for (int i = 0; i < tabCount; i++) {
            tabs[i] = '\t';
        }

        printWriter.write(tabs);
    }

    @Override
    public void visit(Attribute attribute) {
        print(' ' + attribute.getName() + "=\"" + attribute.getValue().toString() + '\"');
    }

    @Override
    public void visitParent(Element element) {
        if (!isNewlined && !isClosed){
            print(">");
            isClosed = true;
        }

        if (!isNewlined){
            print("\n");
            isNewlined = true;
        }

        //doTabs(element.getDepth());
        print("</" + element.getName() + '>');
        isClosed = true;
        isNewlined = false;
    }

    @Override
    public void visit(Text text){
        String textValue = text.getValue();

        if (textValue != null){
            print(">\n");
            //doTabs(text.getDepth());
            print(textValue + '\n');
            isNewlined = true;
            isClosed = false;
        }
    }

    private void print(String string){
        printWriter.write(string);
        //printWriter.flush();
    }
}

/*



 */

/*
private PrintWriter printWriter;
    private StringBuilder stringBuilder = new StringBuilder();

    public FasterCustomVisitor(PrintWriter printWriter){
        this.printWriter = printWriter;
    }

    @Override
    public void visit(Element elem) {
        int length = stringBuilder.length();

        if (length != 0){
            char lastChar = stringBuilder.charAt(length - 1);
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

        //doTabs(elem.getDepth());
        stringBuilder.append('<').append(elem.getName());
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

        //doTabs(element.getDepth());
        stringBuilder.append("</").append(element.getName()).append('>');
    }

    @Override
    public void visit(Text text){
        String textValue = text.getValue();

        if (textValue != null){
            stringBuilder.append(">\n");
            //doTabs(text.getDepth());
            stringBuilder.append(textValue).append('\n');
        }
    }

    private void doTabs(int tabCount) {
        char[] tabs = new char[tabCount];

        for (int i = 0; i < tabCount; i++) {
            tabs[i] = '\t';
        }

        stringBuilder.append(tabs);
    }

    public void printToWriter(Element x){
        while (!(x instanceof Html) ){
            x = x.º();
        }

        x.º();

        printWriter.write(stringBuilder.toString(), 0, stringBuilder.length());
    }
 */