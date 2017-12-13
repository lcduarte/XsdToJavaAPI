package Samples;

public class AttrText extends AbstractElement<AttrText> implements IAttribute{

    private final String text;

    public AttrText(String text) {
        this.text = text;
    }

    public AttrText(String text, String id) {
        this.text = text;
        super.id = id;
    }

    @Override
    public void addAttr(IAttribute attribute) {

    }

    @Override
    public void addChild(IElement child) {

    }

    @Override
    public AttrText self() {
        return this;
    }

    public String getText() {
        return text;
    }

}
