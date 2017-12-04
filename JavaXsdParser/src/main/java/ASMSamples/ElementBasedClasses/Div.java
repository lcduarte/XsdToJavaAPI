package ASMSamples.ElementBasedClasses;

import ASMSamples.PreCreationNeeded.AbstractElement;
import ASMSamples.PreCreationNeeded.CommonAttributeGroup;
import ASMSamples.PreCreationNeeded.Text;

public class Div extends AbstractElement<Div> implements CommonAttributeGroup<Div>, IFlowContent<Div> {

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