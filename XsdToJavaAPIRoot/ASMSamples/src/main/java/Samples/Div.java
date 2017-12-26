package Samples;

public class Div extends AbstractElement<Div> implements ICommonAttributeGroup<Div>, IFlowContent<Div> {

    public Div(){

    }

    public Div(String id) {
        super.id = id;
    }

    public Div(String id, String text){
        super.id = id;
        this.addChild(new Text(text));
    }

    public Div h2(){
        H1 h1 = new H1();
        this.addChild(h1);
        return this;
    }

    @Override
    public Div self() {return this; }

    @Override
    public void acceptInit(Visitor visitor) {
        visitor.initVisit(this);
    }

    @Override
    public void acceptEnd(Visitor visitor) {
        visitor.endVisit(this);
    }
}