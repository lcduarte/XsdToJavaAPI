package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.IElement;
import Samples.HTML.ITextGroup;
import Samples.HTML.Visitor;

public class FirstName<P extends IElement> extends AbstractElement<FirstName<P>, P> implements ITextGroup<FirstName<P>, P> {
    @Override
    public FirstName self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public FirstName cloneElem() {
        return null;
    }
}

