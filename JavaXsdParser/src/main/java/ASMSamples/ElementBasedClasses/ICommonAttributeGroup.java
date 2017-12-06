package ASMSamples.ElementBasedClasses;

import ASMSamples.PreCreationNeeded.AbstractElement;
import ASMSamples.PreCreationNeeded.Element;

public interface ICommonAttributeGroup <T extends AbstractElement<T>> extends Element<T> {

    default T addAttrClass(SomeAttribute var1) {
        ((AbstractElement<T>)this).addAttr(var1);
        return this.self();
    }

}
