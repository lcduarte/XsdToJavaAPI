package org.xmlet.htmlapitest.utils;

import org.xmlet.htmlapi.*;

import java.io.PrintStream;
import java.util.List;

public class CustomVisitor<R> implements ElementVisitor<R> {

    private R model;
    private PrintStream printStream = new PrintStream(System.out);

    public CustomVisitor(){

    }

    public CustomVisitor(R model){
        this.model = model;
    }

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    public <T extends Element> void visit(Element<T, ?> element) {
        printStream.printf("<%s", element.getName());

        element.getAttributes().forEach(attribute -> printStream.printf(" %s=\"%s\"", attribute.getName(), attribute.getValue()));

        printStream.print(">\n");

        if(element.isBound()) {
            List<Element> children = element.cloneElem().bindTo(model).getChildren();
            children.forEach( child -> child.accept(this));
        } else {
            element.getChildren().forEach(item -> item.accept(this));
        }

        printStream.printf("</%s>\n", element.getName());
    }

    @Override
    public <U> void visit(Text<R, U, ?> text){
        String textValue = text.getValue();

        if (textValue != null){
            printStream.println(textValue);
        } else {
            if (model == null){
                throw new RuntimeException("Text node is missing the model. Usage of new CustomVisitor(model) is required.");
            }

            printStream.println(text.getValue(model));
        }
    }

}
