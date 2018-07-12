package org.xmlet.htmlapitest;

import org.junit.Assert;
import org.junit.Test;
import org.xmlet.htmlapifaster.*;
import org.xmlet.htmlapitest.utils.CustomVisitor;

import java.io.File;
import java.lang.Object;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class HtmlApiTest {

    private static final String PACKAGE = "org.xmlet.htmlapi";

    @Test
    public void testGeneratedClassesIntegrity() throws Exception {
        CustomVisitor customVisitor = new CustomVisitor();

        String result = customVisitor.getResult(
                new Html<Html>(customVisitor)
                    .head()
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

        String result = visitor.getResult(
                new Html<>(visitor)
                    .body()
                        .div()
                            .text("This is a regular String."));

        String expected =   "<html>\n" +
                                "\t<body>\n" +
                                    "\t\t<div>\n" +
                                        "\t\t\tThis is a regular String.\n" +
                                    "\t\t</div>\n" +
                                "\t</body>\n" +
                            "</html>";

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testBinderUsage(){
        CustomVisitor visitor = new CustomVisitor();
        List<String> tdValues = new ArrayList<>();

        tdValues.add("val1");
        tdValues.add("val2");
        tdValues.add("val3");

        Html<Html> root = new Html<>(visitor);
        Table<Body<Html<Html>>> table = root.body().table();
        table
            .tr()
                .th()
                    .text("Title")
                .º()
            .º();

        tdValues.forEach(value ->
            table
                .tr()
                    .td()
                        .text(value)
                    .º()
                .º()
        );

        table.º().º();

        String result = visitor.getResult(root);

        String expected =   "<html>\n" +
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
                                "\t</body>\n" +
                            "</html>";

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testAttributeName(){
        Assert.assertEquals("class", new AttrClassString(null).getName());
    }

    @Test
    public void testElementName(){
        CustomVisitor visitor = new CustomVisitor();
        Assert.assertEquals("html", new Html<>(visitor).getName());
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
            Html<Html> dummy = new Html<>(visitor);

            if (generatedFile.getName().endsWith(".class")){
                String absolutePath = generatedFile.getAbsolutePath();

                String className = absolutePath.substring(absolutePath.lastIndexOf('\\') + 1, absolutePath.indexOf(".class"));

                Class<?> klass = ucl.loadClass(PACKAGE + "." + className);

                if (Modifier.isAbstract(klass.getModifiers()) || Modifier.isInterface(klass.getModifiers()) ||
                        klass.isEnum() || klass.getSimpleName().equals("Text") || klass.getSimpleName().equals("Comment")){
                    continue;
                }

                if (AbstractElement.class.isAssignableFrom(klass)){
                    Constructor ctor1 = klass.getConstructor(ElementVisitor.class);
                    Constructor ctor2 = klass.getConstructor(Element.class);
                    Constructor ctor3 = klass.getConstructor(Element.class, String.class);


                    Object elementInstance = ctor1.newInstance(visitor);
                    ctor2.newInstance(dummy);
                    ctor3.newInstance(dummy, "name");

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