import Samples.Div;
import Samples.IElement;
import org.objectweb.asm.util.ASMifier;

import java.util.List;

public class ASMMain {

    public static void main(String[] args){
        try {
            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\ASMSamples\\target\\classes\\Samples\\Div.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
