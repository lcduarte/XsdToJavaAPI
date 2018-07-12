import org.objectweb.asm.util.ASMifier;

public class ASMMain {

    public static void main(String[] args){
        try {
            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\RegularApi\\ASMSamples\\target\\classes\\Samples\\HTML\\SomeAttribute.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\RegularApi\\ASMSamples\\target\\classes\\Samples\\Sequence\\Interfaces\\ANameSequence2.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
