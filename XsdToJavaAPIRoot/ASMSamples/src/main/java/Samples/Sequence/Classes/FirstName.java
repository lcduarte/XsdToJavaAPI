package Samples.Sequence.Classes;

import Samples.HTML.AbstractElement;
import Samples.HTML.ITextGroup;
import Samples.HTML.Visitor;

public class FirstName extends AbstractElement<FirstName> implements ITextGroup<FirstName> {
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

