import Utils.CustomVisitor;
import Utils.Student;
import XsdToJavaAPI.Html5Xsd2JavaApi.*;
import org.junit.Assert;
import org.junit.Test;

public class Html5Xsd2JavaApiTest {

    //TODO Ver a lógica de ter mais do que um tipo possivel para um dado attributo.
    //TODO Verificar se existe mais alguma coisa de errado com as restrições
    //TODO Existem atributos que por vezes tem tipo e outras vezes não tem. Dois attributos diferentes?

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
                .addAttrCharset("UTF-8");

        root.<Link>child("linkId1")
                //.addAttrRel(new AttrRel<>("icon"))
                .addAttrType("text/css")
                .addAttrHref("/assets/images/favicon.png");

        root.<Link>child("linkId2")
                //.addAttrRel(new AttrRel<>("stylesheet"))
                .addAttrType("text/css")
                .addAttrHref("/assets/styles/main.css");

        root.body()
                .addAttrClass("clear")
                .div()
                .header()
                .section()
                .div("divId1")
                .aside("asideId1");

        root.<Div>child("divId1")
                .img()
                .addAttrId("brand")
                .addAttrSrc("./assets/images/logo.png");

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
        new AttrRel<>("Help");
    }

    /**
     *  <xsd:restriction base="xsd:NMTOKEN">
             <xsd:enumeration value="Alternate" />
             <xsd:enumeration value="Appendix" />
             <xsd:enumeration value="Bookmark" />
             <xsd:enumeration value="Chapter" />
             <xsd:enumeration value="Contents" />
             <xsd:enumeration value="Copyright" />
             <xsd:enumeration value="Glossary" />
             <xsd:enumeration value="Help" />
             <xsd:enumeration value="Index" />
             <xsd:enumeration value="Next" />
             <xsd:enumeration value="Prev" />
             <xsd:enumeration value="Section" />
             <xsd:enumeration value="Start" />
             <xsd:enumeration value="Stylesheet" />
             <xsd:enumeration value="Subsection" />
     *  </xsd:restriction>
     *
     *  This attribute creation should fail because the value "Help1" isn't a possible value for the Rel attribute.
     */
    @Test
    public void testRestrictionFailure(){
        try {
            new AttrRel<>("Help1");

            Assert.fail();
        } catch (RestrictionViolationException ignored){ }
    }

    @Test
    public void testVisits(){
        Html rootDoc1 = new Html();
        Html rootDoc2 = new Html();

        rootDoc1.body()
                .div()
                .text("This is a regular String.");

        rootDoc2.body()
                .div()
                .text(Student::getName);

        CustomVisitor customVisitor = new CustomVisitor();

        customVisitor.init(rootDoc1);

        //TODO Não estou a ver como é que passo um Student ao text
    }

}



/*
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title here</title>

    <link rel="icon" type="image/png" href="/assets/images/favicon.png">
    <link rel="stylesheet" href="assets/styles/main.css" type="text/css">
</head>
<body class="clear">
<div id="col-wrap">
    <header id="header">
        <section>
            <div class="logo">
                <img id="brand" src="./assets/images/logo.png">
            </div>
            <aside class="aside narrow">
                <em class="right"> Advertisement <span class="number">1-833-2GET-REV</span></em>
            </aside>
        </section>
    </header>
</div>
</body>
</html>
 */