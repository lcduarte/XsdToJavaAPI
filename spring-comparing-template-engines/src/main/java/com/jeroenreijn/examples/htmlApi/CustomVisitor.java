package com.jeroenreijn.examples.htmlApi;

import org.xmlet.htmlapi.*;

import java.io.PrintWriter;
import java.util.List;

public class CustomVisitor<R> extends ElementVisitor<R> {

    private R model;
    private PrintWriter printWriter;
    //private StringBuilder stringBuilder = new StringBuilder();

    public CustomVisitor(PrintWriter printWriter, R model) {
        this.printWriter = printWriter;
        this.model = model;
    }

    @Override
    public <T extends Element> void sharedVisit(Element<T, ?> element) {
        print(String.format("<%s", element.getName()));

        element.getAttributes().forEach(attribute -> print(String.format(" %s=\"%s\"", attribute.getName(), attribute.getValue())));

        print(">\n");

        if(element.isBound()) {
            List<Element> children = element.cloneElem().bindTo(model).getChildren();
            children.forEach( child -> child.accept(this));
        } else {
            element.getChildren().forEach(item -> item.accept(this));
        }

        print(String.format("</%s>\n", element.getName()));
    }

    @Override
    public void visit(Text text){
        String textValue = text.getValue();

        if (textValue != null){
            print(textValue + "\n");
        }
    }

    @Override
    public <U> void visit(TextFunction<R, U, ?> text){
        if (model == null){
            throw new RuntimeException("Text node is missing the model. Usage of new FasterCustomVisitor(model) is required.");
        }

        print(text.getValue(model).toString() + "\n");
    }

    private void print(String string){
        // stringBuilder.append(string);
        printWriter.write(string);
    }

    /*
    public void performPrint(){
        //printWriter.write(stringBuilder.toString(), 0, stringBuilder.length());
    }
    */
}
