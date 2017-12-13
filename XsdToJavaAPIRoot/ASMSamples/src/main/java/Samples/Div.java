package Samples;

public class Div extends AbstractElement<Div> implements ICommonAttributeGroup<Div>, IFlowContent<Div> {

    public Div(){

    }

    public Div(String id) {
        super.id = id;
    }

    public Div(String id, String text){
        super.id = id;
        this.addChild(new AttrText(text));
    }

    public Div h2(){
        H1 h1 = new H1();
        this.addChild(h1);
        return this;
    }

    @Override
    public Div self() {return this; }
}