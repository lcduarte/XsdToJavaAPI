import org.objectweb.asm.util.ASMifier;

public class ASMMain {

    public static void main(String[] args){
        try {
            /*
            Element element = null;

            PersonalInfo<Element> p = new PersonalInfo<Element>(element);

            PersonalInfoFirstName<PersonalInfo<Element>> f = p.firstName("");
            PersonalInfoLastName<PersonalInfo<Element>> l = f.lastName("");
            PersonalInfoAddress<PersonalInfo<Element>> a = l.address("");
            PersonalInfoCity<PersonalInfo<Element>> c = a.city("");
            PersonalInfoComplete<PersonalInfo<Element>> c1 = c.country("");
            */

            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\Samples\\HTML\\Visitor.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\Samples\\Sequence\\Classes\\PersonalInfoComplete.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\Samples\\Sequence\\Interfaces\\PersonalInfoSequence1.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
