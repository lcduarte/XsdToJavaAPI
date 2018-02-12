import Utils.CustomVisitor;
import Utils.Student;
import XsdToJavaAPI.HtmlApi.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.Object;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HtmlApiTest {

    @Test
    public void testGeneratedClassesIntegrity() throws Exception {
        Html<Html> root = new Html<>();

        Body<Html<Html>> body = root.body();
        Div<Body<Html<Html>>> div = body.div();
        Img<Div<Body<Html<Html>>>> img = div.img();
        Div<Body<Html<Html>>> parent1 = img.$();
        Body<Html<Html>> parent2 = parent1.$();
        Html<Html> parent3 = parent2.$();

        root.head()
                .meta().attrCharset("UTF-8").$()
                .title()
                    .text("Title").$()
                .link().attrType(Enumtype.TEXT_CSS).attrHref("/assets/images/favicon.png").$()
                .link().attrType(Enumtype.TEXT_CSS).attrHref("/assets/styles/main.css").$().$()
            .body().attrClass("clear")
                .div()
                    .header()
                        .section()
                            .div()
                                .img().attrId("brand").attrSrc("./assets/images/logo.png").<Div>$()
                                .aside()
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
        Html<Html> rootDoc = new Html<>();

        rootDoc.body()
                .div()
                .text(Student::getName)
                .text(Student::getNumber);

        CustomVisitor<Student> customVisitor = new CustomVisitor<>(new Student("Luís", 123));

        String expected = "<html>\n<body>\n<div>" +
                            "\nLuís\r\n123\r\n" +
                          "</div>\n</body>\n</html>\n";

        Assert.assertTrue(customVisitPrintAssert(customVisitor, rootDoc, expected));
    }

    @Test
    public void testBinderUsage(){
        List<String> tdValues1 = new ArrayList<>();
        List<String> tdValues2 = new ArrayList<>();

        tdValues1.add("val1");
        tdValues1.add("val2");
        tdValues1.add("val3");

        tdValues2.add("val4");
        tdValues2.add("val5");
        tdValues2.add("val6");

        Html<Html> root = new Html<>();

        root.body()
                .table()
                    .<List<String>>binder((elem, list) ->
                            list.forEach(tdValue ->
                                    elem.tr().td().text(tdValue)
                            )
                    ).$()
                .div();

        CustomVisitor<List<String>> customVisitor1 = new CustomVisitor<>(tdValues1);

        String expected1 = "<html>\n<body>\n<table>\n" +
                                "<tr>\n<td>\nval1\r\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval2\r\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval3\r\n</td>\n</tr>\n" +
                            "</table>\n<div>\n</div>\n</body>\n</html>\n";

        Assert.assertTrue(customVisitPrintAssert(customVisitor1, root, expected1));

        CustomVisitor<List<String>> customVisitor2 = new CustomVisitor<>(tdValues2);

        String expected2 = "<html>\n<body>\n<table>\n" +
                                "<tr>\n<td>\nval4\r\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval5\r\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval6\r\n</td>\n</tr>\n" +
                            "</table>\n<div>\n</div>\n</body>\n</html>\n";

        Assert.assertTrue(customVisitPrintAssert(customVisitor2, root, expected2));
    }

    @Test
    public void testAttributeName(){
        Assert.assertEquals("class", new AttrClass(null).getName());
    }

    @Test
    public void testElementName(){
        Assert.assertEquals("html", new Html().getName());
    }

    @Test
    public void testAttributeCreation(){
        Html root = new Html();

        root.addAttr(new Attribute<>("toto", "tutu"));

        Assert.assertEquals(1, root.getAttributes().size());
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