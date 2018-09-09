import org.junit.Assert;
import org.junit.Test;
import org.xmlet.testMinFaster.*;
import org.xmlet.xsdasmfaster.classes.infrastructure.RestrictionViolationException;

import java.util.Arrays;
import java.util.Collections;

public class XsdAsmMinTest {

    /**
     * Tests if the restrictions in the attribute IntList are functioning. The attribute has
     * a restriction that verifies that the receiving list should have a number of elements between 1 and 5.
     * The expectations are that the code runs without a problem since the length is 1.
     */
    @Test
    public void testListSuccess(){
        AttrIntlistObject.validateRestrictions(Collections.singletonList(1));
    }

    /**
     * Tests if the restrictions in the attribute IntList are functioning. The attribute has
     * a restriction that verifies that the receiving list should have a number of elements between 1 and 5.
     * The expectation is that the constructor of AttrIntlist throws an exception, resulting in the sucess of the test,
     * since the list passed to the constructor exceeds the maximum allowed length.
     */
    @Test(expected = RestrictionViolationException.class)
    public void testListFailed(){
        AttrIntlistObject.validateRestrictions(Arrays.asList(1, 2, 3, 4, 5, 6));
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
        CustomVisitorMin visitor = new CustomVisitorMin();

        new PersonInfoElementContainer<>(visitor)
                .personInfo()
                .firstName("Luis")
                .lastName("Duarte")
                .personAddress("AnAddress")
                .city("Lisbon")
                .country("Portugal")
                .phoneNumber(123456789)
                .º()
                .º();

        String result = visitor.getResult();

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
                "\t\t<phoneNumber>\n" +
                "\t\t\t123456789\n" +
                "\t\t</phoneNumber>\n" +
                "\t</personInfo>\n" +
                "</personInfoElementContainer>";

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

        new StudentGrades<>(visitor)
                .firstName("Luis")
                .lastName("Duarte")
                .gradeNumeric(20)
                .º();

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
                        "</studentGrades>";

        Assert.assertEquals(expected, result);

        visitor = new CustomVisitorMin();

        new StudentGrades<>(visitor)
                .firstName("Luis")
                .lastName("Duarte")
                .gradeQualitative("Excellent")
                .º();

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
                        "</studentGrades>";

        Assert.assertEquals(expected, result);
    }

    /**
     * Tests the name of all the sequence intermediate classes, which should be the same as the base class.
     */
    @Test
    public void testSequenceInnerElementsName(){
        PersonInfo<Element> personInfo = new PersonInfo<>(new CustomVisitorMin());

        Assert.assertEquals("personInfo", personInfo.getName());
        Assert.assertEquals("personInfo", new PersonInfoComplete<>(personInfo).getName());
        Assert.assertEquals("personInfo", new PersonInfoFirstName<>(personInfo).getName());
        Assert.assertEquals("personInfo", new PersonInfoLastName<>(personInfo).getName());
        Assert.assertEquals("personInfo", new PersonInfoCity<>(personInfo).getName());
        Assert.assertEquals("personInfo", new PersonInfoPersonAddress<>(personInfo).getName());
    }

    /**
     * Tests a sequence within a group, which leads to the possibility of having multiple instances
     * of the sequence in the current object.
     */
    @Test
    public void testGroupWithInnerSequence(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        new AName(visitor)
                .elem1("val1")
                .elem2("val2")
                .elem1("val1")
                .elem2("val2")
                .º();

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
                        "</aName>";

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testGroupWithInnerSequencev2(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        AName<Element> elem = new AName<>(visitor);

        elem.elem1("val1")
                .elem2("val2")
                .elem1("val1")
                .elem2("val2")
                .º();

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
                        "</aName>";

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testSequencesContainingSequences() {
        CustomVisitorMin visitor = new CustomVisitorMin();

        new InnerSequences<>(visitor)
                .v1("v1")
                .v2("v2")
                .º();

        String result = visitor.getResult();

        String expected =
                "<innerSequences>\n" +
                        "\t<v1>\n" +
                        "\t\tv1\n" +
                        "\t</v1>\n" +
                        "\t<v2>\n" +
                        "\t\tv2\n" +
                        "\t</v2>\n" +
                        "</innerSequences>";

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testDoubleRestrictionsPass(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        new DoubleRestricted<>(visitor).attrContactDouble(999999999999d);
        new DoubleRestricted<>(visitor).attrContactDouble(999999999999.45d);
        new DoubleRestricted<>(visitor).attrContactDouble(999999999999.9d);
    }

    @Test(expected = RestrictionViolationException.class)
    public void testDoubleRestrictionsFail(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        new DoubleRestricted<>(visitor).attrContactDouble(999999999999.91d);
    }

    @Test
    public void testFloatRestrictionsPass(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        new FloatRestricted<>(visitor).attrContactFloat(99999f);
        new FloatRestricted<>(visitor).attrContactFloat(99999.45f);
        new FloatRestricted<>(visitor).attrContactFloat(99999.9f);
    }

    @Test(expected = RestrictionViolationException.class)
    public void testFloatRestrictionsFail(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        new FloatRestricted<>(visitor).attrContactFloat(99999.91f);
    }

    @Test
    public void testShortRestrictionsPass(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        new ShortRestricted<>(visitor).attrContactShort((short) 9998);
        new ShortRestricted<>(visitor).attrContactShort((short) 9999);
        new ShortRestricted<>(visitor).attrContactShort((short) 10000);
    }

    @Test(expected = RestrictionViolationException.class)
    public void testShortRestrictionsFail(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        new ShortRestricted<>(visitor).attrContactShort((short) 10001);
    }

    @Test
    public void testLongRestrictionsPass(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        new LongRestricted<>(visitor).attrContactLong(9998L);
        new LongRestricted<>(visitor).attrContactLong(9999L);
        new LongRestricted<>(visitor).attrContactLong(10000L);
    }

    @Test(expected = RestrictionViolationException.class)
    public void testLongRestrictionsFail(){
        CustomVisitorMin visitor = new CustomVisitorMin();

        new LongRestricted<>(visitor).attrContactLong(10001L);
    }
}