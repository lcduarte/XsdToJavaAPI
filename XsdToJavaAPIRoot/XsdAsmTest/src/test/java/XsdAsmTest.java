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
    public void testSequenceWithParent(){
        PersonInfoElementContainer<Html> container = new PersonInfoElementContainer<Html>();

        PersonInfo<PersonInfoElementContainer<Html>> personInfo = container.personInfo();

        personInfo.firstName("Luis")
                .lastName("Duarte")
                .personAddress("AnAddress")
                .city("Lisbon")
                .country("Portugal");

        Assert.assertEquals(1, container.getChildren().size());
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

    @Test
    public void testSequenceInnerElementsName(){
        Assert.assertEquals("personInfo", new PersonInfoComplete().getName());
        Assert.assertEquals("personInfo", new PersonInfo().getName());
        Assert.assertEquals("personInfo", new PersonInfoFirstName().getName());
        Assert.assertEquals("personInfo", new PersonInfoLastName().getName());
        Assert.assertEquals("personInfo", new PersonInfoCity().getName());
        Assert.assertEquals("personInfo", new PersonInfoPersonAddress().getName());
    }

    @Test
    public void testGroupWithInnerSequence(){
        AName elem = new AName().elem1("val1")
                                .elem2("val2")
                                .elem1("val1")
                                .elem2("val2");

        Assert.assertEquals(4, elem.getChildren().size());
    }

    @Test
    public void testWpfe(){
        //Alterar o type de attribute para funcionar como o de complexType.?
        Canvas<? extends IElement> canvas = new Canvas<IElement>();

        canvas.canvas_Clip("")
                .inkPresenter("");
    }

    private String getDottedPackage(){
        return XsdAsmUtils.getPackage(apiName).replaceAll("/", ".");
    }
}