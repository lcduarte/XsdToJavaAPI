import Samples.HTML.Div;
import Samples.HTML.H1;
import Samples.HTML.IElement;
import org.objectweb.asm.util.ASMifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASMMain {

    public static void main(String[] args){
        try {

            H1<H1> h1 = new H1<>();
            Div<H1> div1 = new Div<>(h1);
            h1.addChild(div1);
            H1<Div<H1>> h2 = new H1<>(div1);


            H1<Div<IElement>> x1 = new Div<>(new H1<>(new Div<>())).addSomeAttribute(null).º();
            H1<Div<IElement>> x2 = new Div<>(new H1<>(new Div<>())).addAttrClass("").º();


            Div<H1> q = h2.º();

            Div<H1> x = q.cloneElem();


            Div<H1> div1Copy = (Div<H1>) h2.º();
            H1 h1Copy = div1Copy.º();

            Div<Div<H1>> div2 = new Div<>(div1);
            div1.addChild(div2);

            List<Div> res = h1.<Div>find(child ->
                    child.º() instanceof Div && child instanceof Div
            ).collect(Collectors.toList());



            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\ASMSamples\\target\\classes\\Samples\\HTML\\Text.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\ASMSamples\\target\\classes\\Samples\\Sequence\\Interfaces\\PersonalInfoSequence1.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
