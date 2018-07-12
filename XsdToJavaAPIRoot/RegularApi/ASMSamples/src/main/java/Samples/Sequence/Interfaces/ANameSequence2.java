package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.Elem2;

public interface ANameSequence2<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default P elem2(String value){
        º().addChild(new Elem2<>(self()).text(value));
        return º();
    }

}
