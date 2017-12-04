package ASMSamples.ElementBasedClasses;

import ASMSamples.PreCreationNeeded.Element;

public interface IFlowContent<T extends Element> extends Element<T> {
    default public H1 h1(String text) { H1 h1 = new H1(text); addChild(h1); return h1; }
    default public T h1(String id, String text) { H1 h1 = new H1(id, text); addChild(h1); return self(); }
    //default public P p(String text) { P p = new P(text); addChild(p); return p; }
    // ...
}
