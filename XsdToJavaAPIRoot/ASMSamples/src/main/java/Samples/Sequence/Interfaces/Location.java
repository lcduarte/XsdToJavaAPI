package Samples.Sequence.Interfaces;

import Samples.HTML.Element;
import Samples.Sequence.Classes.City;
import Samples.Sequence.Classes.Country;

public interface Location<T extends Element<T, P>, P extends Element> extends Element<T, P> {

    default City<P> city() {
        City<P> city = new City<>(this.º());
        addChild(city);
        return city;
    }

    default Country<P> country() {
        Country<P> country = new Country<>(this.º());
        addChild(country);
        return country;
    }
}
