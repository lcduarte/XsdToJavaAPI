import org.objectweb.asm.util.ASMifier;

public class ASMMain {

    public static void main(String[] args){
        try {
            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\samples\\html\\MiniFlowContent.class"});
            //ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\FasterApi\\ASMSamples\\target\\classes\\samples\\sequence\\AName.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
