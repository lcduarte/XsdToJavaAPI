import Samples.HTML.Div;
import Samples.HTML.Element;
import Samples.HTML.Visitor;
//import Samples.Sequence.Classes.*;
import org.objectweb.asm.util.ASMifier;

public class ASMMain {

    public static void main(String[] args){
        try {
            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\Samples\\HTML\\Div.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\Samples\\Sequence\\Classes\\PersonalInfo.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\Samples\\Sequence\\Interfaces\\PersonalInfoSequence1.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
