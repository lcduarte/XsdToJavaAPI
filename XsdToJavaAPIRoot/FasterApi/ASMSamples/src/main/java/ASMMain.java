import Samples.HTML.Div;
import Samples.HTML.Element;
import Samples.HTML.Text;
import Samples.HTML.Visitor;
//import Samples.Sequence.Classes.*;
import org.objectweb.asm.util.ASMifier;

public class ASMMain {

    public static void main(String[] args){
        try {
            Visitor visitor = new Visitor() {

                @Override
                public void visitElement(Element element) {

                }

                @Override
                public void visitAttribute(String attributeName, String attributeValue) {

                }

                @Override
                public void visitParent(Element elementName) {

                }

                @Override
                public <R> void visitText(Text<? extends Element, R> text) {

                }

                @Override
                public <R> void visitComment(Text<? extends Element, R> comment) {

                }
            };
            Div<Element> element = new Div<Element>(visitor);


            /*PersonalInfo<Div<Element>> p = new PersonalInfo<>(null, visitor);

            PersonalInfoFirstName<Div<Element>> f = p.firstName(1);
            PersonalInfoLastName<Div<Element>> l = f.lastName("");
            PersonalInfoAddress<Div<Element>> a = l.address("");
            PersonalInfoCity<Div<Element>> c = a.city("");
            PersonalInfoComplete<Div<Element>> c1 = c.country("");

            Div<Element> z = c1.º();

            AName<Div<Element>> aName = new AName<>(element);

            ANameElem1<Div<Element>> a1 = aName.elem1("elem1");
            AName<Div<Element>> a2 = a1.elem2("elem2");*/

            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\Samples\\HTML\\H1.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\Samples\\Sequence\\Classes\\AName.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\Samples\\Sequence\\Interfaces\\PersonalInfoSequence1.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
