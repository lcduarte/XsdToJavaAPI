package Utils;

import XsdToJavaAPI.Html5Xsd2JavaApi.AbstractVisitor;
import XsdToJavaAPI.Html5Xsd2JavaApi.Html;
import XsdToJavaAPI.Html5Xsd2JavaApi.IElement;
import XsdToJavaAPI.Html5Xsd2JavaApi.Text;

import java.io.PrintStream;

public class CustomVisitor<T> extends AbstractVisitor<T> {

    private T model;
    private PrintStream DEFAULT_PRINT_STREAM = new PrintStream(System.out);
    private PrintStream printStream = DEFAULT_PRINT_STREAM;

    public CustomVisitor(){

    }

    public CustomVisitor(T model){
        this.model = model;
    }

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    public void init(Html rootDoc1) {
        initVisit(rootDoc1);
        endVisit(rootDoc1);
    }

    @Override
    public <R extends IElement> void initVisit(IElement<R> element) {
        printStream.printf("<%s>\n", element.getClass().getSimpleName());

        element.getChildren().forEach(child -> {
            child.acceptInit(this);
            child.acceptEnd(this);
        });
    }

    @Override
    public <R extends IElement> void endVisit(IElement<R> element) {
        printStream.printf("</%s>\n", element.getClass().getSimpleName());
    }

    @Override
    public void initVisit(Text<T> text){
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

    @Override
    public void endVisit(Text text) {

    }
}
