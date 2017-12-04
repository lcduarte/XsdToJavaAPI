package ASMSamples.PreCreationNeeded;

import ASMSamples.ElementBasedClasses.SomeAttribute;

public interface CommonAttributeGroup<T extends Element> extends Element<T>{
    default T addSomeAttribute(SomeAttribute var1) {
        this.addAttr(var1);
        return self();
    }
}
