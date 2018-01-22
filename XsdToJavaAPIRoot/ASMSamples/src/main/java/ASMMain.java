import Samples.Div;
import org.objectweb.asm.util.ASMifier;

public class ASMMain {

    public static void main(String[] args){
        try {
            Div div  = new Div();

            div.h2();

            Div clone = div.<Div>cloneElem();

            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\ASMSamples\\target\\classes\\Samples\\ITextGroup.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
