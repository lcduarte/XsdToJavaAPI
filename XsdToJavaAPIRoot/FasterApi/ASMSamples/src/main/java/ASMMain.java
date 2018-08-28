import Samples.HTML.Div;
import Samples.HTML.Element;
import Samples.HTML.Visitor;
import Samples.Sequence.Classes.*;
import org.objectweb.asm.util.ASMifier;

public class ASMMain {

    public static void main(String[] args){
        try {
            Visitor visitor = new Visitor() {
                @Override
                public void visitElement(String elementName) {

                }

                @Override
                public void visitAttribute(String attributeName, String attributeValue) {

                }

                @Override
                public void visitParent(String elementName) {

                }

                @Override
                public <R> void visitText(R text) {

                }

                @Override
                public <R> void visitComment(R comment) {

                }
            };
            Div<Element> element = new Div<Element>(visitor);

            PersonalInfo<Div<Element>> p = new PersonalInfo<>(element);

            PersonalInfoFirstName<Div<Element>> f = p.firstName(1);
            PersonalInfoLastName<Div<Element>> l = f.lastName("");
            PersonalInfoAddress<Div<Element>> a = l.address("");
            PersonalInfoCity<Div<Element>> c = a.city("");
            PersonalInfoComplete<Div<Element>> c1 = c.country("");

            Div<Element> z = c1.º();

            AName<Div<Element>> aName = new AName<>(element);

            ANameElem1<Div<Element>> a1 = aName.elem1("elem1");
            AName<Div<Element>> a2 = a1.elem2("elem2");

            Div<Div<Element>> m = z.addCustomElem(new Div<>(z));

            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\Samples\\HTML\\RestrictionValidator.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\Samples\\Sequence\\Classes\\PersonalInfo.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\Samples\\Sequence\\Interfaces\\PersonalInfoSequence1.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
