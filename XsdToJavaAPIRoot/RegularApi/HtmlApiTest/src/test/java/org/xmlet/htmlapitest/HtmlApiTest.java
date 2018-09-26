package org.xmlet.htmlapitest;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.htmlapi.*;
import org.xmlet.htmlapitest.utils.CustomVisitor;
import org.xmlet.htmlapitest.utils.Student;

import java.io.File;
import java.lang.Object;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlApiTest {

    private static final String PACKAGE = "org.xmlet.htmlapi";

    @Test
    public void testGeneratedClassesIntegrity() throws Exception {
        CustomVisitor customVisitor = new CustomVisitor();

        Html<Html> root = new Html<>();

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
                                            .text("HtmlApi is great!");

        root.accept(customVisitor);
        String result = customVisitor.getResult();

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
                            "</html>\n";

        Assert.assertEquals(expected, result);
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
        CustomVisitor visitor = new CustomVisitor();

        Html<Html> rootDoc = new Html<>();

        rootDoc.body()
                    .div()
                        .text("This is a regular String.");

        rootDoc.accept(visitor);

        String result = visitor.getResult();

        String expected =   "<html>\n" +
                                "\t<body>\n" +
                                    "\t\t<div>\n" +
                                        "\t\t\tThis is a regular String.\n" +
                                    "\t\t</div>\n" +
                                "\t</body>\n" +
                            "</html>\n";

        Assert.assertEquals(expected, result);
    }

    /**
     * Tests the custom visitor with a model based on the class Student and its getName method.
     */
    @Test
    public void testVisitsWithModel(){
        CustomVisitor<Student> visitor = new CustomVisitor<>(new Student("Luis", 123));

        Html<Html> rootDoc = new Html<>();

        rootDoc.body()
                    .div()
                        .text(Student::getName)
                        .text(Student::getNumber);

        rootDoc.accept(visitor);
        String result = visitor.getResult();

        String expected =   "<html>\n" +
                                "\t<body>\n" +
                                    "\t\t<div>\n" +
                                        "\t\t\tLuis\n" +
                                        "\t\t\t123\n" +
                                    "\t\t</div>\n" +
                                "\t</body>\n" +
                            "</html>\n";

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testBinderUsage(){
        CustomVisitor<List<String>> customVisitor1 = new CustomVisitor<>(Arrays.asList("val1", "val2", "val3"));
        CustomVisitor<List<String>> customVisitor2 = new CustomVisitor<>(Arrays.asList("val4", "val5", "val6"));

        Html<Element> root = new Html<>()
            .body()
                .table()
                    .tr()
                        .th()
                            .text("Title")
                        .º()
                    .º()
                    .<List<String>>binder((elem, list) ->
                         list.forEach(tdValue ->
                                 elem.tr().td().text(tdValue)
                         )
                    )
                .º()
                .of(FlowContentChoice::div)
            .º();

        root.accept(customVisitor1);
        String result1 = customVisitor1.getResult();

        root.accept(customVisitor2);
        String result2 = customVisitor2.getResult();

        String expected1 =  "<html>\n" +
                                "\t<body>\n" +
                                    "\t\t<table>\n" +
                                        "\t\t\t<tr>\n" +
                                            "\t\t\t\t<th>\n" +
                                                "\t\t\t\t\tTitle\n" +
                                            "\t\t\t\t</th>\n" +
                                        "\t\t\t</tr>\n" +
                                        "\t\t\t<tr>\n" +
                                            "\t\t\t\t<td>\n" +
                                                "\t\t\t\t\tval1\n" +
                                            "\t\t\t\t</td>\n" +
                                        "\t\t\t</tr>\n" +
                                        "\t\t\t<tr>\n" +
                                            "\t\t\t\t<td>\n" +
                                                "\t\t\t\t\tval2\n" +
                                            "\t\t\t\t</td>\n" +
                                        "\t\t\t</tr>\n" +
                                        "\t\t\t<tr>\n" +
                                            "\t\t\t\t<td>\n" +
                                                "\t\t\t\t\tval3\n" +
                                            "\t\t\t\t</td>\n" +
                                        "\t\t\t</tr>\n" +
                                    "\t\t</table>\n" +
                                    "\t\t<div>\n" +
                                    "\t\t</div>\n" +
                                "\t</body>\n" +
                            "</html>\n";

        String expected2 =  "<html>\n" +
                                "\t<body>\n" +
                                    "\t\t<table>\n" +
                                        "\t\t\t<tr>\n" +
                                            "\t\t\t\t<th>\n" +
                                                "\t\t\t\t\tTitle\n" +
                                            "\t\t\t\t</th>\n" +
                                        "\t\t\t</tr>\n" +
                                        "\t\t\t<tr>\n" +
                                            "\t\t\t\t<td>\n" +
                                                "\t\t\t\t\tval4\n" +
                                            "\t\t\t\t</td>\n" +
                                        "\t\t\t</tr>\n" +
                                        "\t\t\t<tr>\n" +
                                            "\t\t\t\t<td>\n" +
                                                "\t\t\t\t\tval5\n" +
                                            "\t\t\t\t</td>\n" +
                                        "\t\t\t</tr>\n" +
                                        "\t\t\t<tr>\n" +
                                            "\t\t\t\t<td>\n" +
                                                "\t\t\t\t\tval6\n" +
                                            "\t\t\t\t</td>\n" +
                                        "\t\t\t</tr>\n" +
                                    "\t\t</table>\n" +
                                    "\t\t<div>\n" +
                                    "\t\t</div>\n" +
                                "\t</body>\n" +
                            "</html>\n";

        Assert.assertEquals(expected1, result1);
        Assert.assertEquals(expected2, result2);
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

                String name = klass.getSimpleName();

                if (AbstractElement.class.isAssignableFrom(klass) && !name.equals("Text") && !name.equals("Comment")){
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
}