package org.xmlet.htmlapitest.utils;

import org.xmlet.htmlapi.*;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class CustomVisitor<R> implements ElementVisitor<R> {

    private R model;
    private BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new DataOutputStream(System.out));

    public CustomVisitor(){

    }

    public CustomVisitor(R model){
        this.model = model;
    }

    public void setBufferedOutputStream(BufferedOutputStream bufferedOutputStream) {
        this.bufferedOutputStream = bufferedOutputStream;
    }

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
    public <U> void visit(Text<R, U, ?> text){
        String textValue = text.getValue();

        if (textValue != null){
            print(textValue + "\n");
        } else {
            if (model == null){
                throw new RuntimeException("Text node is missing the model. Usage of new CustomVisitor(model) is required.");
            }

            print(text.getValue(model).toString() + "\n");
        }
    }

    private void print(String string){
        try {
            bufferedOutputStream.write(string.getBytes(), 0, string.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
