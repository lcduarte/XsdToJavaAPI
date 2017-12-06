package ASMSamples.ElementBasedClasses;

import ASMSamples.PreCreationNeeded.AbstractElement;
import ASMSamples.PreCreationNeeded.Text;

public class Html extends AbstractElement<Html> implements ICommonAttributeGroup<Html> {
    public Html() {
        super();
    }

    public Html(String var1) {
        super();
        addChild(new Text(var1));
    }

    public Html(String var1, String var2) {
        super();
        addChild(new Text(var2));
        super.id = var1;
    }

    public Html self() {
        return this;
    }
}