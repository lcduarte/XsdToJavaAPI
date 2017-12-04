package ASMSamples.PreCreationNeeded;

public class Text extends AbstractElement<Text>{

    private final String text;

    public Text(String text) {
        this.text = text;
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
