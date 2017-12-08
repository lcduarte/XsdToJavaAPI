package XsdParser;

import XsdElements.ElementsWrapper.UnsolvedReference;
import XsdElements.XsdElementBase;

import java.util.ArrayList;
import java.util.List;

public class UnsolvedReferenceItem {

    private UnsolvedReference unsolvedReference;
    private List<XsdElementBase> parents;

    public UnsolvedReferenceItem(UnsolvedReference unsolvedReference){
        this.unsolvedReference = unsolvedReference;
        this.parents = new ArrayList<>();

        this.parents.add(unsolvedReference.getParent());
    }

    public UnsolvedReference getUnsolvedReference() {
        return unsolvedReference;
    }

    public List<XsdElementBase> getParents() {
        return parents;
    }
}
