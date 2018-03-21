package org.xmlet.htmlapitest;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.htmlapi.*;
import org.xmlet.htmlapitest.utils.CustomVisitor;
import org.xmlet.htmlapitest.utils.Student;
import org.xmlet.xsdasm.classes.XsdAsmUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.Object;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlApiTest {

    static final String PACKAGE = "org.xmlet.htmlapi";

    @Test
    public void testGeneratedClassesIntegrity() throws Exception {
        Html<Html> root = new Html<>();

        root.head()
                .meta().attrCharset("UTF-8").º()
                .title()
                    .text("Title").º()
                .link().attrType(EnumTypeContentType.TEXTCSS).attrHref("/assets/images/favicon.png").º()
                .link().attrType(EnumTypeContentType.TEXTCSS).attrHref("/assets/styles/main.css").º().º()
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
                                        .text("1-833-2GET-REV");
    }

    @Test
    public void testFind() throws Exception {
        Html<Html> root = new Html<>();

        Div<Section<Header<Div<Body<Html<Html>>>>>> div = root.head()
                                                            .meta().attrCharset("UTF-8").º()
                                                            .title()
                                                                 .text("Title").º()
                                                            .link().attrType(EnumTypeContentType.TEXTCSS).attrHref("/assets/images/favicon.png").º()
                                                            .link().attrType(EnumTypeContentType.TEXTCSS).attrHref("/assets/styles/main.css").º().º()
                                                            .body().attrClass("clear")
                                                                .div()
                                                                    .header()
                                                                        .section()
                                                                            .div();

        div.img().attrId("brand").attrSrc("./assets/images/logo.png").º()
           .aside()
                .em()
                    .text("Advertisement")
                .span()
                    .text("1-833-2GET-REV");

        List<Div> result = root.<Div>find(child ->
            child instanceof Div && child.º() instanceof Section
        ).collect(Collectors.toList());

        Assert.assertEquals(1, result.size());
        Assert.assertEquals(div, result.get(0));
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
        new AttrRelEnumRelLinkType(EnumRelLinkType.HELP);
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

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");
        table.<List<String>>binder((elem, list) ->
                         list.forEach(tdValue ->
                                 elem.tr().td().text(tdValue)
                         )
                 ).º()
             .div();

        CustomVisitor<List<String>> customVisitor1 = new CustomVisitor<>(tdValues1);

        String expected1 = "<html>\n<body>\n<table>\n" +
                                "<tr>\n<th>\nTitle\r\n</th>\n</tr>\n" +
                                "<tr>\n<td>\nval1\r\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval2\r\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval3\r\n</td>\n</tr>\n" +
                            "</table>\n<div>\n</div>\n</body>\n</html>\n";

        Assert.assertTrue(customVisitPrintAssert(customVisitor1, root, expected1));
        Assert.assertTrue(customVisitPrintAssert(customVisitor1, root, expected1));

        CustomVisitor<List<String>> customVisitor2 = new CustomVisitor<>(tdValues2);

        String expected2 = "<html>\n<body>\n<table>\n" +
                                "<tr>\n<th>\nTitle\r\n</th>\n</tr>\n" +
                                "<tr>\n<td>\nval4\r\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval5\r\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval6\r\n</td>\n</tr>\n" +
                            "</table>\n<div>\n</div>\n</body>\n</html>\n";

        Assert.assertTrue(customVisitPrintAssert(customVisitor2, root, expected2));
    }

    @Test
    public void testAttributeName(){
        Assert.assertEquals("class", new AttrClassString(null).getName());
    }

    @Test
    public void testElementName(){
        Assert.assertEquals("html", new Html().getName());
    }

    @Test
    public void testAttributeCreation(){
        Html root = new Html();

        root.addAttr(new BaseAttribute<>("toto", "tutu"));

        Assert.assertEquals(1, root.getAttributes().size());
    }

    @Test
    public void testTextExceptions(){
        try {
            new Text<>(new Html(), "dummy").addAttr(new AttrHrefString(""));
            Assert.fail();
        } catch (UnsupportedOperationException ignored){}
    }

    @Test
    public void testAllElements() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        URL targetUrl = Element.class.getClassLoader().getResource("");

        Assert.assertNotNull(targetUrl);

        String target = targetUrl.getPath().replaceAll("test-", "") + "org/xmlet/htmlapi";

        File generatedObjectsFolder = new File(target);
        File[] generatedFiles = generatedObjectsFolder.listFiles();

        assert generatedFiles != null;

        URLClassLoader ucl = new URLClassLoader(new URL[]{ new URL("file://" + target )});

        for (File generatedFile : generatedFiles) {
            CustomVisitor visitor = new CustomVisitor();

            if (generatedFile.getName().endsWith(".class")){
                String absolutePath = generatedFile.getAbsolutePath();

                String className = absolutePath.substring(absolutePath.lastIndexOf('\\') + 1, absolutePath.indexOf(".class"));

                Class<?> klass = ucl.loadClass(PACKAGE + "." + className);

                if (Modifier.isAbstract(klass.getModifiers()) || Modifier.isInterface(klass.getModifiers()) || klass.isEnum() || klass.getName().contains("Text")){
                    continue;
                }

                if (AbstractElement.class.isAssignableFrom(klass)){
                    Constructor ctor1 = klass.getConstructor();
                    Constructor ctor2 = klass.getConstructor(String.class);
                    Constructor ctor3 = klass.getConstructor(Element.class);
                    Constructor ctor4 = klass.getConstructor(Element.class, String.class);

                    Object elementInstance = ctor1.newInstance();
                    ctor2.newInstance("name");
                    ctor3.newInstance(new Object[]{null});
                    ctor4.newInstance(null, "name");

                    Method[] methods = klass.getMethods();

                    for (Method method : methods) {
                        if (method.getParameterCount() == 0 && Element.class.isAssignableFrom(method.getReturnType())){
                            method.invoke(elementInstance);
                        }

                        if (method.getParameterCount() == 1 && method.getName().startsWith("attr")){
                            Class<?> paramType = method.getParameterTypes()[0];
                            if (!paramType.isEnum()){
                                if (paramType.equals(String.class) || paramType.equals(Object.class)){
                                    method.invoke(elementInstance, new Object[]{null});
                                }

                                if (paramType.equals(Short.class)){
                                    method.invoke(elementInstance, (short) 0);
                                }

                                if (paramType.equals(Double.class)){
                                    method.invoke(elementInstance, (double) 0);
                                }

                                if (paramType.equals(Integer.class)){
                                    method.invoke(elementInstance, 0);
                                }

                                if (paramType.equals(Float.class)){
                                    method.invoke(elementInstance, 0f);
                                }

                                if (paramType.equals(Boolean.class)){
                                    method.invoke(elementInstance, false);
                                }
                            }
                        }
                    }

                    ((Element)elementInstance).accept(visitor);
                }
            }
        }
    }

    @Test
    public void testEnums(){
        new AttrRelEnumRelLinkType(EnumRelLinkType.HELP);
        new AttrTargetEnumTargetBrowsingContext(EnumTargetBrowsingContext.BLANK);
        new AttrFormtargetEnumFormtargetBrowsingContext(EnumFormtargetBrowsingContext.BLANK);
        new AttrMediaEnumMediaMediaType(EnumMediaMediaType.ALL);
        new AttrAsyncEnumAsyncscript(EnumAsyncscript.ASYNC);
        new AttrCheckedEnumCheckedcommand(EnumCheckedcommand.CHECKED);
        new AttrControlsEnumControlsaudio(EnumControlsaudio.CONTROLS);
        new AttrControlsEnumControlsvideo(EnumControlsvideo.CONTROLS);
        new AttrAutofocusEnumAutofocusbutton(EnumAutofocusbutton.AUTOFOCUS);
        new AttrAutofocusEnumAutofocuskeygen(EnumAutofocuskeygen.AUTOFOCUS);
        new AttrAutofocusEnumAutofocusselect(EnumAutofocusselect.AUTOFOCUS);
        new AttrAutofocusEnumAutofocustextarea(EnumAutofocustextarea.AUTOFOCUS);
        new AttrAutobufferEnumAutobufferaudio(EnumAutobufferaudio.AUTOBUFFER);
        new AttrAutobufferEnumAutobuffervideo(EnumAutobuffervideo.AUTOBUFFER);
        new AttrAutocompleteEnumAutocompleteform(EnumAutocompleteform.ON);
        new AttrAutoplayEnumAutoplayaudio(EnumAutoplayaudio.AUTOPLAY);
        new AttrCheckedEnumCheckedinput(EnumCheckedinput.CHECKED);
        new AttrDisabledEnumDisabledcommand(EnumDisabledcommand.DISABLED);
        new AttrContenteditableEnumContenteditable(EnumContenteditable.FALSE);
        new AttrDeferEnumDeferscript(EnumDeferscript.DEFER);
        new AttrDirEnumDir(EnumDir.RTL);
        new AttrDisabledEnumDisabledbutton(EnumDisabledbutton.AUTOFOCUS);
        new AttrDisabledEnumDisabledcommand(EnumDisabledcommand.DISABLED);
        new AttrDisabledEnumDisabledinput(EnumDisabledinput.DISABLED);
        new AttrDisabledEnumDisabledkeygen(EnumDisabledkeygen.DISABLED);
        new AttrDisabledEnumDisabledoptgroup(EnumDisabledoptgroup.DISABLED);
        new AttrDisabledEnumDisabledoption(EnumDisabledoption.DISABLED);
        new AttrDisabledEnumDisabledselect(EnumDisabledselect.AUTOFOCUS);
        new AttrDisabledEnumDisabledtextarea(EnumDisabledtextarea.DISABLED);
        new AttrDraggableEnumDraggable(EnumDraggable.AUTO);
        new AttrEnctypeEnumEnctypeform(EnumEnctypeform.MULTIPARTFORMDATA);
        new AttrAutoplayEnumAutoplayvideo(EnumAutoplayvideo.AUTOPLAY);
        new AttrFormenctypeEnumFormenctypebutton(EnumFormenctypebutton.APPLICATIONXWWWFORMURLENCODED);
        new AttrFormenctypeEnumFormenctypeinput(EnumFormenctypeinput.APPLICATIONXWWWFORMURLENCODED);
        new AttrFormmethodEnumFormmethodbutton(EnumFormmethodbutton.DELETE);
        new AttrFormmethodEnumFormmethodinput(EnumFormmethodinput.DELETE);
        new AttrFormnovalidateEnumFormnovalidatebutton(EnumFormnovalidatebutton.FORMNOVALIDATE);
        new AttrFormnovalidateEnumFormnovalidateinput(EnumFormnovalidateinput.FORMNOVALIDATE);
        new AttrHiddenEnumHidden(EnumHidden.HIDDEN);
        new AttrHttpequivEnumHttpequivmeta(EnumHttpequivmeta.REFRESH);
        new AttrIsmapEnumIsmapimg(EnumIsmapimg.ISMAP);
        new AttrKeytypeEnumKeytypekeygen(EnumKeytypekeygen.RSA);
        new AttrLoopEnumLoopaudio(EnumLoopaudio.LOOP);
        new AttrLoopEnumLoopvideo(EnumLoopvideo.LOOP);
        new AttrMethodEnumMethodform(EnumMethodform.DELETE);
        new AttrMultipleEnumMultipleselect(EnumMultipleselect.MULTIPLE);
        new AttrNameEnumNameBrowsingContext(EnumNameBrowsingContext.BLANK);
        new AttrNovalidateEnumNovalidateform(EnumNovalidateform.NOVALIDATE);
        new AttrOpenEnumOpendetails(EnumOpendetails.OPEN);
        new AttrReadonlyEnumReadonlytextarea(EnumReadonlytextarea.READONLY);
        new AttrRequiredEnumRequiredtextarea(EnumRequiredtextarea.REQUIRED);
        new AttrReversedEnumReversedol(EnumReversedol.REVERSED);
        new AttrRunatEnumRunat(EnumRunat.SERVER);
        new AttrSandboxEnumSandboxiframe(EnumSandboxiframe.ALLOWFORMS);
        new AttrScopedEnumScopedstyle(EnumScopedstyle.SCOPED);
        new AttrScopeEnumScopeth(EnumScopeth.COL);
        new AttrSeamlessEnumSeamlessiframe(EnumSeamlessiframe.SEAMLESS);
        new AttrSelectedEnumSelectedoption(EnumSelectedoption.SELECTED);
        new AttrShapeEnumShapearea(EnumShapearea.CIRCLE);
        new AttrSpellcheckEnumSpellcheck(EnumSpellcheck.FALSE);
        new AttrTypeEnumTypebutton(EnumTypebutton.BUTTON);
        new AttrTypeEnumTypecommand(EnumTypecommand.CHECKBOX);
        new AttrTypeEnumTypeinput(EnumTypeinput.BUTTON);
        new AttrTypeEnumTypemenu(EnumTypemenu.CONTEXT);
        new AttrTypeEnumTypescript(EnumTypescript.TEXTECMASCRIPT);
        new AttrTypeEnumTypeSimpleContentType(EnumTypeSimpleContentType.TEXTASA);
        new AttrTypeEnumTypestyle(EnumTypestyle.TEXTCSS);
        new AttrWrapEnumWraptextarea(EnumWraptextarea.HARD);
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