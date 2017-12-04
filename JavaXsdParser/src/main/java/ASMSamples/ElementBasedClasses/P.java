package ASMSamples.ElementBasedClasses;

import ASMSamples.PreCreationNeeded.AbstractElement;
import ASMSamples.PreCreationNeeded.Text;

public class P extends AbstractElement<P> implements IFlowContent<P> {
    public P() {}
    public P(String text) {this.children.add(new Text(text));}
    public P(String id, String text) {this.id = id;
        this.children.add(new Text(text));}
    public P self() {return this; }
}