package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.City;
import Samples.Sequence.Classes.Country;

public interface Location<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default City<P> city() {
        return new City<>(this.º());
    }

    default Country<P> country() {
        return new Country<>(this.º());
    }

}
