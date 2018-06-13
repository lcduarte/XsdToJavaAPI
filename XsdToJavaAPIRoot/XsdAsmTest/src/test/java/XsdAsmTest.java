import org.junit.Assert;
import org.junit.Test;
import org.xmlet.TestObjects.*;
import org.xmlet.xsdasm.classes.XsdAsmUtils;
import org.xmlet.xsdasm.main.XsdAsmMain;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class XsdAsmTest {

    private static final String ANDROID_FILE_NAME = XsdAsmTest.class.getClassLoader().getResource("android.xsd").getPath();
    private static final String MIN_TESTS_FILE_NAME = XsdAsmTest.class.getClassLoader().getResource("test_min.xsd").getPath();
    private static final String ANDROID_API_NAME = "Android";
    private static final String MIN_TESTS_API_NAME = "MinTests";

    static {
        XsdAsmMain.main(new String[]{ANDROID_FILE_NAME, ANDROID_API_NAME});
        XsdAsmMain.main(new String[]{MIN_TESTS_FILE_NAME, MIN_TESTS_API_NAME});
    }

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
        PersonInfoElementContainer<Html> container = new PersonInfoElementContainer<>();

        PersonInfo<PersonInfoElementContainer<Html>> personInfo = container.personInfo();

        PersonInfoComplete personInfoComplete = personInfo.firstName("Luis")
                .lastName("Duarte")
                .personAddress("AnAddress")
                .city("Lisbon")
                .country("Portugal");

        Assert.assertEquals(1, container.getChildren().size());
        Assert.assertEquals(5, personInfoComplete.getChildren().size());
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
        StudentGradesComplete studentGradesCompleteNumeric = new StudentGrades().firstName("Luis")
                .lastName("Duarte")
                .gradeNumeric(String.valueOf(20));

        Assert.assertEquals(3, studentGradesCompleteNumeric.getChildren().size());

        StudentGradesComplete studentGradesCompleteQualitative = new StudentGrades().firstName("Luis")
                .lastName("Duarte")
                .gradeQualitative("Excellent");

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
        AName elem = new AName().elem1("val1")
                .elem2("val2")
                .elem1("val1")
                .elem2("val2");

        Assert.assertEquals(4, elem.getChildren().size());
    }

    /**
     * A simple test to test the interface depth in action.
     */
    @Test
    public void testWpfe(){
        Canvas<Html> canvas = new Canvas<>();

        canvas.canvasClip("")
                .inkPresenter("");
    }

    @Test
    public void testAndroidHierarchy(){
        new RelativeLayout<>()
                .attrAndroidGravity(EnumAndroidGravity.CENTER) /* Method from RelativeLayout */
                .attrAndroidAddStatesFromChildren(EnumAndroidAddStatesFromChildren.TRUE) /* Method from ViewGroup */
                .attrAndroidLayoutX(null); /* Method from View */

        Assert.assertTrue(View.class.isAssignableFrom(ViewGroup.class));
        Assert.assertTrue(ViewGroup.class.isAssignableFrom(RelativeLayout.class));
    }

    /**
     * <LinearLayout
     *      orientation="vertical"
     *      width="match_parent"
     *      height="wrap_content">
     *
     *      <LinearLayout
     *          orientation="horizontal"
     *          width="match_parent"
     *          height="wrap_content">
     *
     *          <ImageView
     *              width="wrap_content"
     *              height="wrap_content">
     *
     *          <TextView
     *              width="match_parent"
     *              height="wrap_content"
     *              lines="2">
     *      </LinearLayout>
     * </LinearLayout>
     */
    @Test
    public void testSimpleAndroidLayout(){
        new LinearLayout<>()
                .attrAndroidOrientation(EnumAndroidOrientation.VERTICAL)
                .attrAndroidLayoutWidth("match_parent")
                .attrAndroidLayoutHeight("wrap_content")
                .addChild(
                        new LinearLayout()
                                .attrAndroidOrientation(EnumAndroidOrientation.HORIZONTAL)
                                .attrAndroidLayoutWidth("match_parent")
                                .attrAndroidLayoutHeight("wrap_content")
                                .addChild(
                                        new ImageView()
                                                .attrAndroidLayoutWidth("wrap_content")
                                                .attrAndroidLayoutHeight("wrap_content")
                                ).addChild(
                                new TextView()
                                        .attrAndroidWidth("match_parent")
                                        .attrAndroidHeight("weight_content")
                                        .attrAndroidLines("2")
                        )
                );
    }

    @Test
    public void testGeneratedClassesIntegrity() throws Exception {
        testIntegrity(ANDROID_API_NAME);
        testIntegrity(MIN_TESTS_API_NAME);
    }

    @SuppressWarnings("Duplicates")
    private void testIntegrity(String apiName) throws Exception {
        File generatedObjectsFolder = new File(XsdAsmUtils.getDestinationDirectory(apiName));
        File[] generatedFiles = generatedObjectsFolder.listFiles();

        assert generatedFiles != null;

        URLClassLoader ucl = new URLClassLoader(
                new URL[]{
                        new URL("file://" + XsdAsmUtils.getDestinationDirectory(apiName)) });

        for (File generatedFile : generatedFiles) {
            if (generatedFile.getName().endsWith(".class")){
                String absolutePath = generatedFile.getAbsolutePath();

                String className = absolutePath.substring(absolutePath.lastIndexOf('\\') + 1, absolutePath.indexOf(".class"));

                ucl.loadClass( getDottedPackage(apiName) + className);
            }
        }
    }

    private String getDottedPackage(String apiName){
        return XsdAsmUtils.getPackage(apiName).replaceAll("/", ".");
    }
}