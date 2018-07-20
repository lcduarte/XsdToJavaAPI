import org.junit.Assert;
import org.junit.Test;
import org.xmlet.wpfeFaster.*;

public class XsdAsmWpfeTest {

    /**
     * A simple test to test the interface depth in action.
     */
    @Test
    public void testWpfe(){
        CustomVisitorWpfe visitor = new CustomVisitorWpfe();

        Canvas<Element> canvas = new Canvas<>(visitor);

        canvas.canvasClip("clip")
                .inkPresenter("ink").º();

        String result = visitor.getResult(canvas);

        String expected =
                "<canvas>\n" +
                    "\t<canvasClip>\n" +
                        "\t\tclip\n" +
                    "\t</canvasClip>\n" +
                    "\t<inkPresenter>\n" +
                        "\t\tink\n" +
                    "\t</inkPresenter>\n" +
                "</canvas>";

        Assert.assertEquals(expected, result);
    }

}
