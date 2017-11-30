package ASMSamples.ElementBasedClasses;

import ASMSamples.PreCreationNeeded.AbstractElement;

public class H1 extends AbstractElement<H1> implements IFlowContent<H1> {
    public H1() {}
    public H1(String text) {}
    public H1(String id, String text) {}
    public H1 self() {return this; }
}
