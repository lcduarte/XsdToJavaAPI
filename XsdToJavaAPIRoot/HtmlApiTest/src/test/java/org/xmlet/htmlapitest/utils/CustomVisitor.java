package org.xmlet.htmlapitest.utils;

import org.xmlet.htmlapi.AbstractElementVisitor;
import org.xmlet.htmlapi.Element;
import org.xmlet.htmlapi.Html;
import org.xmlet.htmlapi.Text;

import java.io.PrintStream;
import java.util.List;

public class CustomVisitor<R> extends AbstractElementVisitor<R> {

    private R model;
    private PrintStream DEFAULT_PRINT_STREAM = new PrintStream(System.out);
    private PrintStream printStream = DEFAULT_PRINT_STREAM;

    public CustomVisitor(){

    }

    public CustomVisitor(R model){
        this.model = model;
    }

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public <T extends Element> void visit(Element<T, ?> element) {
        printStream.printf("<%s>\n", element.getName());

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

            printStream.println(text.getValue((R) model));
        }
    }

}
