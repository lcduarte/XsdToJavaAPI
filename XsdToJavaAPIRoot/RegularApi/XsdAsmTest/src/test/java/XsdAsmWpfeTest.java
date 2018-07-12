import org.junit.Assert;
import org.junit.Test;
import org.xmlet.wpfe.Canvas;
import org.xmlet.wpfe.Element;

public class XsdAsmWpfeTest {

    /**
     * A simple test to test the interface depth in action.
     */
    @Test
    public void testWpfe(){
        CustomVisitorWpfe visitor = new CustomVisitorWpfe();

        Canvas<Element> canvas = new Canvas<>();

        canvas.canvasClip("clip")
              .inkPresenter("ink");

        canvas.accept(visitor);

        String result = visitor.getResult();

        String expected =
                "<canvas>\n" +
                    "\t<canvasClip>\n" +
                        "\t\tclip\n" +
                    "\t</canvasClip>\n" +
                    "\t<inkPresenter>\n" +
                        "\t\tink\n" +
                    "\t</inkPresenter>\n" +
                "</canvas>\n";

        Assert.assertEquals(expected, result);
    }}