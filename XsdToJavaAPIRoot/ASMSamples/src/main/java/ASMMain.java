import org.objectweb.asm.util.ASMifier;

import javax.xml.datatype.XMLGregorianCalendar;

public class ASMMain {

    public static void main(String[] args){
        try {
            ASMifier.main(new String[]{"D:\\ISEL\\Tese\\Desenvolvimento\\Repositorio\\XsdToJavaAPI\\XsdToJavaAPIRoot\\ASMSamples\\target\\classes\\Samples\\SomeAttribute.class"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
