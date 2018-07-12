import org.junit.Assert;
import org.junit.Test;
import org.xmlet.testMin.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class XsdAsmMinTest {

    /**
     * Tests if the restrictions in the attribute IntList are functioning. The attribute has
     * a restriction that verifies that the receiving list should have a number of elements between 1 and 5.
     * The expectations are that the code runs without a problem since the length is 1.
     */
    @Test
    public void testListSuccess(){
        List<Integer> intList = new ArrayList<>();

        intList.add(1);

        new AttrIntlistObject(intList);
    }

    /**
     * Tests if the restrictions in the attribute IntList are functioning. The attribute has
     * a restriction that verifies that the receiving list should have a number of elements between 1 and 5.
     * The expectation is that the constructor of AttrIntlist throws an exception, resulting in the sucess of the test,
     * since the list passed to the constructor exceeds the maximum allowed length.
     */
    @Test
    public void testListFailed(){
        List<Integer> intList = new ArrayList<>();

        intList.add(1);
        intList.add(2);
        intList.add(3);
        intList.add(4);
        intList.add(5);
        intList.add(6);

        try {
            new AttrIntlistObject(intList);
            Assert.fail();
        } catch (RestrictionViolationException ignored){

        }
    }

    /**
     * Based on the following xsd:
     * <xs:element name="personInfo">
     <xs:complexType>
     <xs:sequence>
     <xs:element name="firstName" type="xs:string"/>
     <xs:element name="lastName" type="xs:string"/>
     <xs:element name="personAddress" type="xs:string"/>
     <xs:element name="city" type="xs:string"/>
     <xs:element name="country" type="xs:string"/>
     </xs:sequence>
     </xs:complexType>
     </xs:element>
     * This test allows to demonstrate two things:
     *      * The sequence interfaces work properly, since the methods exist and all the intermediate classes
     *          are coherent.
     *      * The resulting parent, PersonInfoElementContainer, only has a child.
     *      * The resulting element has all the element as children.
     */
    @Test
    public void testSequenceWithParent(){
        CustomVisitorMin customVisitor = new CustomVisitorMin<>();

        PersonInfoElementContainer<Element> container = new PersonInfoElementContainer<>();

        PersonInfo<PersonInfoElementContainer<Element>> personInfo = container.personInfo();

        PersonInfo<PersonInfoElementContainer<Element>> personInfoComplete = personInfo;

        personInfo.firstName("Luis")
                .lastName("Duarte")
                .personAddress("AnAddress")
                .city("Lisbon")
                .country("Portugal");

        Assert.assertEquals(1, container.getChildren().size());
        Assert.assertEquals(5, personInfoComplete.getChildren().size());

        container.accept(customVisitor);

        String result = customVisitor.getResult();

        String expected =   "<personInfoElementContainer>\n" +
                                "\t<personInfo>\n" +
                                    "\t\t<firstName>\n" +
                                        "\t\t\tLuis\n" +
                                    "\t\t</firstName>\n" +
                                    "\t\t<lastName>\n" +
                                        "\t\t\tDuarte\n" +
                                    "\t\t</lastName>\n" +
                                    "\t\t<personAddress>\n" +
                                        "\t\t\tAnAddress\n" +
                                    "\t\t</personAddress>\n" +
                                    "\t\t<city>\n" +
                                        "\t\t\tLisbon\n" +
                                    "\t\t</city>\n" +
                                    "\t\t<country>\n" +
                                        "\t\t\tPortugal\n" +
                                    "\t\t</country>\n" +
                                "\t</personInfo>\n" +
                            "</personInfoElementContainer>\n";

        Assert.assertEquals(expected, result);
    }

    /**
     * Based on:
     *  <xs:element name="studentGrades">
     <xs:complexType>
     <xs:sequence>
     <xs:element name="firstName" type="xs:string"/>
     <xs:element name="lastName" type="xs:string"/>
     <xs:group>
     <xsd:all>
     <xs:element name="gradeNumeric" />
     <xs:element name="gradeQualitative" />
     </xsd:all>
     </xs:group>
     </xs:sequence>
     </xs:complexType>
     </xs:element>
     * This test allows to test a sequence with a group as a member.
     * As we can see we can choose to add a numeric grade or a qualitative, but not both.
     */
    @Test
    public void testSequenceWithGroups(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        StudentGrades studentGradesCompleteNumeric = new StudentGrades();

        studentGradesCompleteNumeric
                .firstName("Luis")
                .lastName("Duarte")
                .gradeNumeric(String.valueOf(20));

        studentGradesCompleteNumeric.accept(visitor);
        String result = visitor.getResult();

        String expected =
                        "<studentGrades>\n" +
                            "\t<firstName>\n" +
                                "\t\tLuis\n" +
                            "\t</firstName>\n" +
                            "\t<lastName>\n" +
                                "\t\tDuarte\n" +
                            "\t</lastName>\n" +
                            "\t<gradeNumeric>\n" +
                                "\t\t20\n" +
                            "\t</gradeNumeric>\n" +
                        "</studentGrades>\n";

        Assert.assertEquals(3, studentGradesCompleteNumeric.getChildren().size());
        Assert.assertEquals(expected, result);

        visitor = new CustomVisitorMin();

        StudentGrades studentGradesCompleteQualitative = new StudentGrades();

        studentGradesCompleteQualitative
                .firstName("Luis")
                .lastName("Duarte")
                .gradeQualitative("Excellent");

        studentGradesCompleteQualitative.accept(visitor);

        result = visitor.getResult();

        expected =
                    "<studentGrades>\n" +
                        "\t<firstName>\n" +
                            "\t\tLuis\n" +
                        "\t</firstName>\n" +
                        "\t<lastName>\n" +
                            "\t\tDuarte\n" +
                        "\t</lastName>\n" +
                        "\t<gradeQualitative>\n" +
                            "\t\tExcellent\n" +
                        "\t</gradeQualitative>\n" +
                    "</studentGrades>\n";

        Assert.assertEquals(expected, result);
        Assert.assertEquals(3, studentGradesCompleteQualitative.getChildren().size());
    }

    /**
     * Tests the name of all the sequence intermediate classes, which should be the same as the base class.
     */
    @Test
    public void testSequenceInnerElementsName(){
        Assert.assertEquals("personInfo", new PersonInfoComplete().getName());
        Assert.assertEquals("personInfo", new PersonInfo().getName());
        Assert.assertEquals("personInfo", new PersonInfoFirstName().getName());
        Assert.assertEquals("personInfo", new PersonInfoLastName().getName());
        Assert.assertEquals("personInfo", new PersonInfoCity().getName());
        Assert.assertEquals("personInfo", new PersonInfoPersonAddress().getName());
    }

    /**
     * Tests a sequence within a group, which leads to the possibility of having multiple instances
     * of the sequence in the current object.
     */
    @Test
    public void testGroupWithInnerSequence(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        AName elem = new AName<>()
                .elem1("val1")
                .elem2("val2")
                .elem1("val1")
                .elem2("val2");

        elem.accept(visitor);
        String result = visitor.getResult();

        String expected =
                "<aName>\n" +
                    "\t<elem1>\n" +
                        "\t\tval1\n" +
                    "\t</elem1>\n" +
                    "\t<elem2>\n" +
                        "\t\tval2\n" +
                    "\t</elem2>\n" +
                    "\t<elem1>\n" +
                        "\t\tval1\n" +
                    "\t</elem1>\n" +
                    "\t<elem2>\n" +
                        "\t\tval2\n" +
                    "\t</elem2>\n" +
                "</aName>\n";

        Assert.assertEquals(expected, result);
        Assert.assertEquals(4, elem.getChildren().size());
    }

    @Test
    public void testGroupWithInnerSequencev2(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        AName<Element> elem = new AName<>();

        elem.elem1("val1")
            .elem2("val2")
            .elem1("val1")
            .elem2("val2");

        elem.accept(visitor);
        String result = visitor.getResult();

        String expected =
                "<aName>\n" +
                    "\t<elem1>\n" +
                        "\t\tval1\n" +
                    "\t</elem1>\n" +
                    "\t<elem2>\n" +
                        "\t\tval2\n" +
                    "\t</elem2>\n" +
                    "\t<elem1>\n" +
                        "\t\tval1\n" +
                    "\t</elem1>\n" +
                    "\t<elem2>\n" +
                        "\t\tval2\n" +
                    "\t</elem2>\n" +
                "</aName>\n";

        Assert.assertEquals(expected, result);
        Assert.assertEquals(4, elem.getChildren().size());
    }

    @Test
    public void testSequencesContainingSequences() {
        CustomVisitorMin visitor = new CustomVisitorMin();

        InnerSequences<Element> innerSequences = new InnerSequences<>();

        innerSequences.v1("v1")
                      .v2("v2");

        innerSequences.accept(visitor);
        String result = visitor.getResult();

        String expected =
                "<innerSequences>\n" +
                    "\t<v1>\n" +
                        "\t\tv1\n" +
                    "\t</v1>\n" +
                    "\t<v2>\n" +
                        "\t\tv2\n" +
                    "\t</v2>\n" +
                "</innerSequences>\n";

        Assert.assertEquals(expected, result);
        Assert.assertEquals(2, innerSequences.getChildren().size());
    }
}
