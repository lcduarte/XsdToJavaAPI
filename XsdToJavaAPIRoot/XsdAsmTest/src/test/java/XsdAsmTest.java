import XsdAsm.XsdAsmUtils;
import XsdToJavaAPI.TestObjects.*;
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

        new AttrIntlist(intList);
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
            new AttrIntlist(intList);
            Assert.fail();
        } catch (RestrictionViolationException ignored){

        }
    }

    @Test
    public void testSequence(){
        PersonInfoComplete personInfoComplete = new PersonInfo().firstName("Luis")
                                                                .lastName("Duarte")
                                                                .personAddress("AnAddress")
                                                                .city("Lisbon")
                                                                .country("Portugal");

        Assert.assertEquals(5, personInfoComplete.getChildren().size());
    }

    @Test
    public void testSequenceWithGroups(){
        StudentGradesComplete studentGradesCompleteNumeric = new StudentGrades().firstName("Luis")
                .lastName("Duarte")
                .gradeNumeric(String.valueOf(20));

        Assert.assertEquals(3, studentGradesCompleteNumeric.getChildren().size());

        StudentGradesComplete studentGradesCompleteQualitative = new StudentGrades().firstName("Luis")
                .lastName("Duarte")
                .gradeQualitative("Excellent");

        Assert.assertEquals(3, studentGradesCompleteQualitative.getChildren().size());
    }

    private String getDottedPackage(){
        return XsdAsmUtils.getPackage(apiName).replaceAll("/", ".");
    }
}