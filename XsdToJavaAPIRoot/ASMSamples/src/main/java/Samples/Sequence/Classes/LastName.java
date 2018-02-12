package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.IElement;
import Samples.HTML.ITextGroup;
import Samples.HTML.Visitor;

public class LastName<P extends IElement> extends AbstractElement<LastName<P>, P> implements ITextGroup<LastName<P>, P> {
    @Override
    public LastName self() {
        return this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Override
    public LastName cloneElem() {
        return null;
    }
}

