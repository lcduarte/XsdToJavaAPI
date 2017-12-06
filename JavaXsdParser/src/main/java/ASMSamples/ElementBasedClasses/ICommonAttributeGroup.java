package ASMSamples.ElementBasedClasses;

import ASMSamples.PreCreationNeeded.Element;

public interface ICommonAttributeGroup <T extends Element> extends Element<T> {

    default T addAttrClass(SomeAttribute var1) {
        this.addAttr(var1);
        return this.self();
    }

}
