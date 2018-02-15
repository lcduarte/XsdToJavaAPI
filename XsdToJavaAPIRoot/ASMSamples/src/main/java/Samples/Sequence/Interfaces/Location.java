package Samples.Sequence.Interfaces;

import Samples.HTML.IElement;
import Samples.Sequence.Classes.City;
import Samples.Sequence.Classes.Country;

public interface Location<T extends IElement<T, P>, P extends IElement> extends IElement<T, P> {

    default City<P> city() {
        City<P> city = new City<>(this.getParent());
        addChild(city);
        return city;
    }

    default Country<P> country() {
        Country<P> country = new Country<>(this.getParent());
        addChild(country);
        return country;
    }
}
