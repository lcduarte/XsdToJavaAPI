import org.objectweb.asm.util.ASMifier;

public class ASMMain {

    public static void main(String[] args){
        try {
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\ASMSamples\\target\\classes\\Samples\\SomeAttribute.class"});
            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\ASMSamples\\target\\classes\\Samples\\RestrictionValidator.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
