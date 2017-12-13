package Samples;

public class H1 extends AbstractElement<H1> implements IFlowContent<H1> {
    public H1() {}
    public H1(String text) {this.children.add(new AttrText(text));}
    public H1(String id, String text) {this.id = id;
        this.children.add(new AttrText(text));}

    public H1 self() {return this; }
}
