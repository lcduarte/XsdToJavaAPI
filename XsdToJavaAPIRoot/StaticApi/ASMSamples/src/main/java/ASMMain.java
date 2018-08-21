import Samples.HTML.AbstractElement;
import Samples.HTML.Div;
import Samples.HTML.MiniFlowContent;
import org.objectweb.asm.util.ASMifier;

public class ASMMain {

    public static void main(String[] args){
        try {
            Div<AbstractElement> d = Div.div();

            Div<Div<AbstractElement>> d2 = d.<Div<AbstractElement>>div();

            AbstractElement d4 = d2.º();

            Div<Div> d1 = d.<Div<Div>>text("");

            AbstractElement d3 = d1.º();

            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\StaticApi\\ASMSamples\\target\\classes\\Samples\\HTML\\Div.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\StaticApi\\ASMSamples\\target\\classes\\Samples\\Sequence\\Classes\\PersonalInfoFirstName.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\StaticApi\\ASMSamples\\target\\classes\\Samples\\Sequence\\Interfaces\\PersonalInfoSequence1.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
