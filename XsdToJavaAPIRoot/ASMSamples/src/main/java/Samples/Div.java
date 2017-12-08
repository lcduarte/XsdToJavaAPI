package Samples;

public class Div extends AbstractElement<Div> implements ICommonAttributeGroup<Div>, IFlowContent<Div> {

    public Div(){

    }

    public Div(String text){
        this.children.add(new Text(text));
    }

    public Div(String id, String text){
        this.id = id;
        this.children.add(new Text(text));
    }

    @Override
    public Div self() {return this; }
}