package ASMSamples.PreCreationNeeded;

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
    public void addAttr(Attribute attribute) {

    }

    @Override
    public void addChild(AbstractElement child) {

    }

    @Override
    public Text self() {
        return this;
    }

    public String getText() {
        return text;
    }

}
