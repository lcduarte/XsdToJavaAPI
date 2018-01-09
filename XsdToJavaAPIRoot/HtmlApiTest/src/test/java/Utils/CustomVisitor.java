package Utils;

import XsdToJavaAPI.Html5Xsd2JavaApi.AbstractVisitor;
import XsdToJavaAPI.Html5Xsd2JavaApi.Html;
import XsdToJavaAPI.Html5Xsd2JavaApi.IElement;
import XsdToJavaAPI.Html5Xsd2JavaApi.Text;

import java.io.PrintStream;

public class CustomVisitor<R> extends AbstractVisitor<R> {

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

    public void init(Html rootDoc1) {
        rootDoc1.accept(this);
    }

    @Override
    public <T extends IElement> void initVisit(IElement<T> element) {
        printStream.printf("<%s>\n", element.getName());
    }

    @Override
    public <T extends IElement> void endVisit(IElement<T> element) {
        printStream.printf("</%s>\n", element.getName());
    }

    @Override
    public void initVisit(Text<R> text){
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

    @Override
    public void endVisit(Text<R> text) {

    }
}
