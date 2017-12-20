import XsdToJavaAPI.Html5Xsd2JavaApi.*;
import org.junit.Assert;
import org.junit.Test;

public class Html5Xsd2JavaApiTest {

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
                .addAttrCharset(new AttrCharset<>( "UTF-8"));

        root.<Link>child("linkId1")
                //.addAttrRel(new AttrRel<>("icon"))
                .addAttrType(new AttrType<>("image/png"))
                .addAttrHref(new AttrHref<>("/assets/images/favicon.png"));

        root.<Link>child("linkId2")
                //.addAttrRel(new AttrRel<>("stylesheet"))
                .addAttrType(new AttrType<>("text/css"))
                .addAttrHref(new AttrHref<>("/assets/styles/main.css"));

        root.body()
                .addAttrClass(new AttrClass<>("clear"))
                .div()
                .header()
                .section()
                .div("divId1")
                .aside("asideId1");

        root.<Div>child("divId1")
                .img()
                .addAttrId(new AttrId<>("brand"))
                .addAttrSrc(new AttrSrc<>("./assets/images/logo.png"));

        root.<Aside>child("asideId1")
                .em()
                .text("Advertisement")
                .span()
                .text("1-833-2GET-REV");
    }

    @Test
    public void testRestrictionSuccess(){
        new AttrRel<>("Help");
    }

    @Test
    public void testRestrictionFailure(){
        try {
            new AttrRel<>("Help1");

            Assert.fail();
        } catch (RestrictionViolationException ignored){ }
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