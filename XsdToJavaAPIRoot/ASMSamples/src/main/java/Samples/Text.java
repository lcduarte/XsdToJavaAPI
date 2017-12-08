package Samples;

public class Text extends AbstractElement<Text>{

    private final String text;

    public Text(String text) {
        this.text = text;
    }

    public Text(String text, String id) {
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
    public Text self() {
        return this;
    }

    public String getText() {
        return text;
    }

}
