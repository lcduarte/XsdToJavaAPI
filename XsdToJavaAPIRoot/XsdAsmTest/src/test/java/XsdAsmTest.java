import XsdAsm.XsdAsmUtils;
import XsdToJavaAPI.TestObjects.AttrIntlist;
import XsdToJavaAPI.TestObjects.RestrictionViolationException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class XsdAsmTest {

    private static String apiName = "TestObjects";

    @Test
    public void testListSuccess(){
        List<Integer> intList = new ArrayList<Integer>();

        intList.add(1);

        AttrIntlist attrIntlist = new AttrIntlist(intList);
    }

    @Test
    public void testListFailed(){
        List<Integer> intList = new ArrayList<Integer>();

        intList.add(1);
        intList.add(2);
        intList.add(3);
        intList.add(4);
        intList.add(5);
        intList.add(6);

        try {
            AttrIntlist attrIntlist = new AttrIntlist(intList);
            Assert.fail();
        } catch (RestrictionViolationException ignored){

        }
    }

    private String getDottedPackage(){
        return XsdAsmUtils.getPackage(apiName).replaceAll("/", ".");
    }
}