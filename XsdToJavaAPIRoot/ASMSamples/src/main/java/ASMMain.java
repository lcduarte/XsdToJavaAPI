import Samples.HTML.Div;
import Samples.HTML.H1;
import Samples.HTML.IElement;
import org.objectweb.asm.util.ASMifier;

import java.util.List;

public class ASMMain {

    public static void main(String[] args){
        try {

            H1<H1> h1 = new H1<>();
            Div<H1> div1 = new Div<>(h1);
            H1<Div<H1>> h2 = new H1<>(div1);


            H1<Div<IElement>> x1 = new Div<>(new H1<>(new Div<>())).addSomeAttribute(null).getParent();
            H1<Div<IElement>> x2 = new Div<>(new H1<>(new Div<>())).addAttrClass("").getParent();


            Div q = h2.getParent();

            Div<H1> div1Copy = (Div<H1>) h2.getParent();
            H1 h1Copy = div1Copy.getParent();

            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\ASMSamples\\target\\classes\\Samples\\HTML\\Div.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\ASMSamples\\target\\classes\\Samples\\Sequence\\Interfaces\\PersonalInfoSequence1.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
