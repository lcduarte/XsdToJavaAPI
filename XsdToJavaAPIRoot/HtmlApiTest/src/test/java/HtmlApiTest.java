import Utils.CustomVisitor;
import Utils.Student;
import XsdToJavaAPI.HtmlApi.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class HtmlApiTest {

    @Test
    public void testGeneratedClassesIntegrity() throws Exception {
        Html root = new Html();

        root.head()
                .meta("metaId1")
                .title("titleId1")
                .link("linkId1")
                .link("linkId2");

        root.<Title>child("titleId1")
                .text("Title");

        root.<Meta>child("metaId1")
                .attrCharset("UTF-8");

        root.<Link>child("linkId1")
                //.addAttrRel(new AttrRel<>("icon"))
                .attrType(Enumtype.TEXT_CSS)
                .attrHref("/assets/images/favicon.png");

        root.<Link>child("linkId2")
                //.addAttrRel(new AttrRel<>("stylesheet"))
                .attrType(Enumtype.TEXT_CSS)
                .attrHref("/assets/styles/main.css");

        root.body()
                .attrClass("clear")
                .div()
                .header()
                .section()
                .div("divId1")
                .aside("asideId1");

        root.<Div>child("divId1")
                .img()
                .attrId("brand")
                .attrSrc("./assets/images/logo.png");


        root.<Aside>child("asideId1")
                .em()
                .text("Advertisement")
                .span()
                .text("1-833-2GET-REV");
    }

    /**
     *  <xsd:restriction base="xsd:NMTOKEN">
             (...)
             <xsd:enumeration value="Help" />
             (...)
     *  </xsd:restriction>
     *
     *  This attribute creation should be successful because the value "Help" is a possible value for the Rel attribute.
     */
    @Test
    public void testRestrictionSuccess(){
        new AttrRel(Enumrel.HELP);
    }

    /**
     * Tests the custom visitor without applying any model to text<T>
     */
    @Test
    public void testVisitsWithoutModel(){
        Html rootDoc = new Html();

        rootDoc.body()
                .div()
                .text("This is a regular String.");

        CustomVisitor customVisitor = new CustomVisitor();

        String expected = "<html>\n<body>\n<div>\nThis is a regular String.\r\n</div>\n</body>\n</html>\n";

        Assert.assertTrue(customVisitPrintAssert(customVisitor, rootDoc, expected));
    }

    /**
     * Tests the custom visitor with a model based on the class Student and its getName method.
     */
    @Test
    public void testVisitsWithModel(){
        Html rootDoc = new Html();

        rootDoc.body()
                .div()
                .text(Student::getName);

        CustomVisitor<Student> customVisitor = new CustomVisitor<>(new Student("Luís"));

        String expected = "<html>\n<body>\n<div>\nLuís\r\n</div>\n</body>\n</html>\n";

        Assert.assertTrue(customVisitPrintAssert(customVisitor, rootDoc, expected));
    }

    @Test
    public void testAttributeName(){
        Assert.assertEquals("class", new AttrClass(null).getName());
    }

    @Test
    public void testElementName(){
        Assert.assertEquals("html", new Html().getName());
    }

    private boolean customVisitPrintAssert(CustomVisitor customVisitor, Html rootDoc, String expected){
        boolean result = false;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(buffer, false, "utf-8");

            customVisitor.setPrintStream(printStream);
            customVisitor.init(rootDoc);

            String content = new String(buffer.toByteArray(), StandardCharsets.UTF_8);

            result = expected.equals(content);

            printStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

}