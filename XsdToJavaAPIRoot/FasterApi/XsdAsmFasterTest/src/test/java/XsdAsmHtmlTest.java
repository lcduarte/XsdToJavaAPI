import org.junit.Assert;
import org.junit.Test;
import org.xmlet.htmlFaster.*;

public class XsdAsmHtmlTest {

    @Test
    public void testGeneratedClassesIntegrity() throws Exception {
        CustomVisitorHtml customVisitor = new CustomVisitorHtml();

        Html<Html> root = new Html<>(customVisitor);

        String result = customVisitor.getResult(
                root.head()
                        .comment("This is a comment.")
                        .meta().attrCharset("UTF-8").º()
                        .title()
                            .text("Title").º()
                        .link().attrType(EnumTypeContentType.TEXT_CSS).attrHref("/assets/images/favicon.png").º()
                        .link().attrType(EnumTypeContentType.TEXT_CSS).attrHref("/assets/styles/main.css").º().º()
                    .body().attrClass("clear")
                        .div()
                            .header()
                                .section()
                                    .div()
                                        .img().attrId("brand").attrSrc("./assets/images/logo.png").º()
                                        .aside()
                                            .em()
                                                .text("Advertisement")
                                            .span()
                                                .text("HtmlApi is great!"));

        String expected =   "<html>\n" +
                                "\t<head>\n" +
                                    "\t\t<!-- This is a comment. -->\n" +
                                    "\t\t<meta charset=\"UTF-8\">\n" +
                                    "\t\t</meta>\n" +
                                    "\t\t<title>\n" +
                                        "\t\t\tTitle\n" +
                                    "\t\t</title>\n" +
                                    "\t\t<link type=\"text/css\" href=\"/assets/images/favicon.png\">\n" +
                                    "\t\t</link>\n" +
                                    "\t\t<link type=\"text/css\" href=\"/assets/styles/main.css\">\n" +
                                    "\t\t</link>\n" +
                                "\t</head>\n" +
                                "\t<body class=\"clear\">\n" +
                                    "\t\t<div>\n" +
                                        "\t\t\t<header>\n" +
                                            "\t\t\t\t<section>\n" +
                                                "\t\t\t\t\t<div>\n" +
                                                    "\t\t\t\t\t\t<img id=\"brand\" src=\"./assets/images/logo.png\">\n" +
                                                    "\t\t\t\t\t\t</img>\n" +
                                                    "\t\t\t\t\t\t<aside>\n" +
                                                        "\t\t\t\t\t\t\t<em>\n" +
                                                            "\t\t\t\t\t\t\t\tAdvertisement\n" +
                                                            "\t\t\t\t\t\t\t\t<span>\n" +
                                                                "\t\t\t\t\t\t\t\t\tHtmlApi is great!\n" +
                                                            "\t\t\t\t\t\t\t\t</span>\n" +
                                                        "\t\t\t\t\t\t\t</em>\n" +
                                                    "\t\t\t\t\t\t</aside>\n" +
                                                "\t\t\t\t\t</div>\n" +
                                            "\t\t\t\t</section>\n" +
                                        "\t\t\t</header>\n" +
                                    "\t\t</div>\n" +
                                "\t</body>\n" +
                            "</html>";

        Assert.assertEquals(expected, result);
    }
}
