package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.ITextGroup;
import Samples.HTML.Visitor;

public class LastName extends AbstractElement<LastName> implements ITextGroup<LastName> {
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

