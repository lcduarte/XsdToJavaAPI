package org.xmlet.htmlapitest;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.htmlapi.*;
import org.xmlet.htmlapitest.utils.CustomVisitor;
import org.xmlet.htmlapitest.utils.Student;

import java.io.*;
import java.lang.Object;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
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
                                        .text("HtmlApi is great!");

        CustomVisitor customVisitor = new CustomVisitor();

        root.accept(customVisitor);
    }

    @Test
    public void testFind() throws Exception {
        Html<Html> root = new Html<>();

        Div<Section<Header<Div<Body<Html<Html>>>>>> div = root.head()
                                                            .meta().attrCharset("UTF-8").º()
                                                            .title()
                                                                 .text("Title").º()
                                                            .link().attrType(EnumTypeContentType.TEXT_CSS).attrHref("/assets/images/favicon.png").º()
                                                            .link().attrType(EnumTypeContentType.TEXT_CSS).attrHref("/assets/styles/main.css").º().º()
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
        Html<Html> rootDoc = new Html<>();

        rootDoc.body()
                .div()
                .text("This is a regular String.");

        CustomVisitor customVisitor = new CustomVisitor();

        String expected = "<html>\n<body>\n<div>\nThis is a regular String.\n</div>\n</body>\n</html>\n";

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

        CustomVisitor<Student> customVisitor = new CustomVisitor<>(new Student("Luis", 123));

        String expected = "<html>\n<body>\n<div>" +
                            "\nLuis\n123\n" +
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
                                "<tr>\n<th>\nTitle\n</th>\n</tr>\n" +
                                "<tr>\n<td>\nval1\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval2\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval3\n</td>\n</tr>\n" +
                            "</table>\n<div>\n</div>\n</body>\n</html>\n";

        Assert.assertTrue(customVisitPrintAssert(customVisitor1, root, expected1));
        Assert.assertTrue(customVisitPrintAssert(customVisitor1, root, expected1));

        CustomVisitor<List<String>> customVisitor2 = new CustomVisitor<>(tdValues2);

        String expected2 = "<html>\n<body>\n<table>\n" +
                                "<tr>\n<th>\nTitle\n</th>\n</tr>\n" +
                                "<tr>\n<td>\nval4\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval5\n</td>\n</tr>\n" +
                                "<tr>\n<td>\nval6\n</td>\n</tr>\n" +
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
        new AttrTargetEnumTargetBrowsingContext(EnumTargetBrowsingContext._BLANK);
        new AttrFormtargetEnumFormtargetBrowsingContext(EnumFormtargetBrowsingContext._BLANK);
        new AttrMediaEnumMediaMediaType(EnumMediaMediaType.ALL);
        new AttrAsyncEnumAsyncScript(EnumAsyncScript.ASYNC);
        new AttrCheckedEnumCheckedCommand(EnumCheckedCommand.CHECKED);
        new AttrControlsEnumControlsAudio(EnumControlsAudio.CONTROLS);
        new AttrControlsEnumControlsVideo(EnumControlsVideo.CONTROLS);
        new AttrAutofocusEnumAutofocusButton(EnumAutofocusButton.AUTOFOCUS);
        new AttrAutofocusEnumAutofocusKeygen(EnumAutofocusKeygen.AUTOFOCUS);
        new AttrAutofocusEnumAutofocusSelect(EnumAutofocusSelect.AUTOFOCUS);
        new AttrAutofocusEnumAutofocusTextarea(EnumAutofocusTextarea.AUTOFOCUS);
        new AttrAutobufferEnumAutobufferAudio(EnumAutobufferAudio.AUTOBUFFER);
        new AttrAutobufferEnumAutobufferVideo(EnumAutobufferVideo.AUTOBUFFER);
        new AttrAutocompleteEnumAutocompleteForm(EnumAutocompleteForm.ON);
        new AttrAutoplayEnumAutoplayAudio(EnumAutoplayAudio.AUTOPLAY);
        new AttrCheckedEnumCheckedInput(EnumCheckedInput.CHECKED);
        new AttrDisabledEnumDisabledCommand(EnumDisabledCommand.DISABLED);
        new AttrContenteditableEnumContenteditable(EnumContenteditable.FALSE);
        new AttrDeferEnumDeferScript(EnumDeferScript.DEFER);
        new AttrDirEnumDir(EnumDir.RTL);
        new AttrDisabledEnumDisabledButton(EnumDisabledButton.AUTOFOCUS);
        new AttrDisabledEnumDisabledCommand(EnumDisabledCommand.DISABLED);
        new AttrDisabledEnumDisabledInput(EnumDisabledInput.DISABLED);
        new AttrDisabledEnumDisabledKeygen(EnumDisabledKeygen.DISABLED);
        new AttrDisabledEnumDisabledOptgroup(EnumDisabledOptgroup.DISABLED);
        new AttrDisabledEnumDisabledOption(EnumDisabledOption.DISABLED);
        new AttrDisabledEnumDisabledSelect(EnumDisabledSelect.AUTOFOCUS);
        new AttrDisabledEnumDisabledTextarea(EnumDisabledTextarea.DISABLED);
        new AttrDraggableEnumDraggable(EnumDraggable.AUTO);
        new AttrEnctypeEnumEnctypeForm(EnumEnctypeForm.MULTIPART_FORM_DATA);
        new AttrAutoplayEnumAutoplayVideo(EnumAutoplayVideo.AUTOPLAY);
        new AttrFormenctypeEnumFormenctypeButton(EnumFormenctypeButton.APPLICATION_X_WWW_FORM_URLENCODED);
        new AttrFormenctypeEnumFormenctypeInput(EnumFormenctypeInput.APPLICATION_X_WWW_FORM_URLENCODED);
        new AttrFormmethodEnumFormmethodButton(EnumFormmethodButton.DELETE);
        new AttrFormmethodEnumFormmethodInput(EnumFormmethodInput.DELETE);
        new AttrFormnovalidateEnumFormnovalidateButton(EnumFormnovalidateButton.FORMNOVALIDATE);
        new AttrFormnovalidateEnumFormnovalidateInput(EnumFormnovalidateInput.FORMNOVALIDATE);
        new AttrHiddenEnumHidden(EnumHidden.HIDDEN);
        new AttrHttpEquivEnumHttpEquivMeta(EnumHttpEquivMeta.REFRESH);
        new AttrIsmapEnumIsmapImg(EnumIsmapImg.ISMAP);
        new AttrKeytypeEnumKeytypeKeygen(EnumKeytypeKeygen.RSA);
        new AttrLoopEnumLoopAudio(EnumLoopAudio.LOOP);
        new AttrLoopEnumLoopVideo(EnumLoopVideo.LOOP);
        new AttrMethodEnumMethodForm(EnumMethodForm.DELETE);
        new AttrMultipleEnumMultipleSelect(EnumMultipleSelect.MULTIPLE);
        new AttrNameEnumNameBrowsingContext(EnumNameBrowsingContext._BLANK);
        new AttrNovalidateEnumNovalidateForm(EnumNovalidateForm.NOVALIDATE);
        new AttrOpenEnumOpenDetails(EnumOpenDetails.OPEN);
        new AttrReadonlyEnumReadonlyTextarea(EnumReadonlyTextarea.READONLY);
        new AttrRequiredEnumRequiredTextarea(EnumRequiredTextarea.REQUIRED);
        new AttrReversedEnumReversedOl(EnumReversedOl.REVERSED);
        new AttrRunatEnumRunat(EnumRunat.SERVER);
        new AttrSandboxEnumSandboxIframe(EnumSandboxIframe.ALLOW_FORMS);
        new AttrScopedEnumScopedStyle(EnumScopedStyle.SCOPED);
        new AttrScopeEnumScopeTh(EnumScopeTh.COL);
        new AttrSeamlessEnumSeamlessIframe(EnumSeamlessIframe.SEAMLESS);
        new AttrSelectedEnumSelectedOption(EnumSelectedOption.SELECTED);
        new AttrShapeEnumShapeArea(EnumShapeArea.CIRCLE);
        new AttrSpellcheckEnumSpellcheck(EnumSpellcheck.FALSE);
        new AttrTypeEnumTypeButton(EnumTypeButton.BUTTON);
        new AttrTypeEnumTypeCommand(EnumTypeCommand.CHECKBOX);
        new AttrTypeEnumTypeInput(EnumTypeInput.BUTTON);
        new AttrTypeEnumTypeMenu(EnumTypeMenu.CONTEXT);
        new AttrTypeEnumTypeScript(EnumTypeScript.TEXT_ECMASCRIPT);
        new AttrTypeEnumTypeSimpleContentType(EnumTypeSimpleContentType.TEXT_ASA);
        new AttrTypeEnumTypeStyle(EnumTypeStyle.TEXT_CSS);
        new AttrWrapEnumWrapTextarea(EnumWrapTextarea.HARD);
    }

    private boolean customVisitPrintAssert(CustomVisitor customVisitor, Html rootDoc, String expected){
        boolean result = false;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(buffer);
            BufferedOutputStream bos = new BufferedOutputStream(dataOutputStream);

            customVisitor.setBufferedOutputStream(bos);
            rootDoc.accept(customVisitor);
            bos.flush();

            String content = new String(buffer.toByteArray());

            result = expected.equals(content);

            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}