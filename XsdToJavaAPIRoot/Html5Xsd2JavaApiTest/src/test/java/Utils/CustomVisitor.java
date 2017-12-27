package Utils;

import XsdToJavaAPI.Html5Xsd2JavaApi.*;

public class CustomVisitor extends AbstractVisitor {

    public void init(Html rootDoc1) {
        initVisit(rootDoc1);
        endVisit(rootDoc1);
    }

    @Override
    public <T extends IElement> void initVisit(IElement<T> element) {
        element.getChildren().forEach(child -> {
            child.acceptInit(this);
            child.acceptEnd(this);
        });
    }

    @Override
    public <T extends IElement> void endVisit(IElement<T> element) {

    }

    public void initVisit(Html html){
        System.out.println("Abre Html");

        super.initVisit(html);
    }

    @Override
    public void endVisit(Html html) {
        System.out.println("Fecha Html");
    }

    public void initVisit(Body body){
        System.out.println("Abre Body");

        super.initVisit(body);
    }

    @Override
    public void endVisit(Body body) {
        System.out.println("Fecha Body");
    }

    public void initVisit(Div div){
        System.out.println("Abre Div");

        super.initVisit(div);
    }

    @Override
    public void endVisit(Div div) {
        System.out.println("Fecha Div");
    }

    public void initVisit(Text text){
        System.out.println("Abre Text");

        System.out.println(text.getValue());
    }

    @Override
    public void endVisit(Text text) {
        System.out.println("Fecha Text");
    }
}
