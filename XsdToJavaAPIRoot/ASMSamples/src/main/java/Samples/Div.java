package Samples;

public class Div extends AbstractElement<Div> implements ICommonAttributeGroup<Div>, IFlowContent<Div> {

    public Div() {
        super();
    }

    public Div(IElement parent) {
        super(parent);
    }

    public Div(IElement parent,String id) {
        super(parent, id);
    }

    public H1 h2(){
        H1 var1 = new H1(this);
        this.addChild(var1);
        return var1;
    }

    public H1 h2(String id){
        H1 var1 = new H1(this, id);
        this.addChild(var1);
        return var1;
    }

    @Override
    public Div self() {return this; }

    @Override
    public void accept(Visitor visitor) {
        visitor.initVisit(this);

        getChildren().forEach(child -> child.accept(visitor));

        visitor.endVisit(this);
    }

    @Override
    public Div cloneElem() {
        return this.clone(new Div());
    }
}