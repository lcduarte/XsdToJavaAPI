import Utils.CustomVisitor;
import Utils.Student;
import XsdToJavaAPI.HtmlApi.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HtmlApiTest {

    @Test
    public void testGeneratedClassesIntegrity() throws Exception {
        Html root = new Html();

        root.head()
                .meta().attrCharset("UTF-8").<Head>$()
                .title()
                    .text("Title").<Head>$()
                .link().attrType(Enumtype.TEXT_CSS).attrHref("/assets/images/favicon.png").<Head>$()
                .link().attrType(Enumtype.TEXT_CSS).attrHref("/assets/styles/main.css").<Head>$().<Html>$()
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
        Html rootDoc = new Html();

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
        Html root = new Html();

        Table table = root.body().table();

        List<String> tdValues = new ArrayList<>();

        tdValues.add("val1");
        tdValues.add("val2");
        tdValues.add("val3");

        table.binder(elem ->
            tdValues.forEach(tdValue ->
                elem.tr().td().text(tdValue)
            )
        );

        CustomVisitor customVisitor = new CustomVisitor();

        String expected1 = "<html>\n<body>\n<table>\n" +
                                "<tr>\n<td>\nval1\r\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval2\r\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval3\r\n</td>\n</tr>\n" +
                            "</table>\n</body>\n</html>\n";

        Assert.assertTrue(customVisitPrintAssert(customVisitor, root, expected1));

        tdValues.clear();

        tdValues.add("val4");
        tdValues.add("val5");
        tdValues.add("val6");

        String expected2 = "<html>\n<body>\n<table>\n" +
                "<tr>\n<td>\nval4\r\n</td>\n</tr>\n" +
                "<tr>\n<td>\nval5\r\n</td>\n</tr>\n" +
                "<tr>\n<td>\nval6\r\n</td>\n</tr>\n" +
                "</table>\n</body>\n</html>\n";

        Assert.assertTrue(customVisitPrintAssert(customVisitor, root, expected2));

        ArrayList<String> otherTdValues = new ArrayList<>();

        otherTdValues.add("val7");
        otherTdValues.add("val8");
        otherTdValues.add("val9");

        table.binder(elem ->
                otherTdValues.forEach(tdValue ->
                        elem.tr().td().text(tdValue)
                )
        );

        String expected3 = "<html>\n<body>\n<table>\n" +
                "<tr>\n<td>\nval7\r\n</td>\n</tr>\n" +
                "<tr>\n<td>\nval8\r\n</td>\n</tr>\n" +
                "<tr>\n<td>\nval9\r\n</td>\n</tr>\n" +
                "</table>\n</body>\n</html>\n";

        Assert.assertTrue(customVisitPrintAssert(customVisitor, root, expected3));
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