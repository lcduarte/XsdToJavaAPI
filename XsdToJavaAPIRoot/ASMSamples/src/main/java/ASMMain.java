import Samples.EnumExample;
import Samples.EnumUsage;
import org.objectweb.asm.util.ASMifier;

import java.lang.reflect.Constructor;

public class ASMMain {

    public static void main(String[] args){
        try {
            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\ASMSamples\\target\\classes\\Samples\\AbstractAttribute.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
