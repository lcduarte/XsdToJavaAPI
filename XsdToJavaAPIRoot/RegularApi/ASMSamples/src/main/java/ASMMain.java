import samples.html.Div;
import samples.html.Element;
import samples.sequence.*;
import org.objectweb.asm.util.ASMifier;

public class ASMMain {

    public static void main(String[] args){
        try {

            Div<Element> div = new Div<>();

            PersonalInfo<Div<Element>> p = new PersonalInfo<>(div);

            PersonalInfoFirstName<PersonalInfo<Div<Element>>> f = p.firstName("");
            PersonalInfoLastName<PersonalInfo<Div<Element>>> l = f.lastName("");
            PersonalInfoAddress<PersonalInfo<Div<Element>>> a = l.address("");
            PersonalInfoCity<PersonalInfo<Div<Element>>> c = a.city("");
            PersonalInfoComplete<PersonalInfo<Div<Element>>> c1 = c.country("");

            PersonalInfo<Div<Element>> x = c1.º();

            AName<Div<Element>> aName = new AName<>(div);

            ANameElem1<AName<Div<Element>>, Div<Element>> a1 = aName.elem1("elem1");
            AName<Div<Element>> a2 = a1.elem2("elem2");

            int pSize = p.getChildren().size();

            int aSize = aName.getChildren().size();

            assert pSize == 5;
            assert aSize == 2;

            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\RegularApi\\ASMSamples\\target\\classes\\samples\\html\\Div.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\RegularApi\\ASMSamples\\target\\classes\\samples\\sequence\\PersonalInfoFirstName.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
