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
import java.util.Arrays;
import java.util.List;

public class HtmlApiTest {

    private static final String PACKAGE = "org.xmlet.htmlapi";

    public void dummy(){
        CustomVisitor customVisitor = new CustomVisitor();

        Body<Element> body = new Body<>(customVisitor);

        body.table();

        Html<Element> html = new Html<>(customVisitor);

        html.attrDir(EnumDir.LTR);
        html.attrManifest("manifestValue");
    }

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

                if (Modifier.isAbstract(klass.getModifiers()) || Modifier.isInterface(klass.getModifiers()) || klass.isEnum()){
                    continue;
                }

                boolean isXmletElement = Arrays.asList(klass.getInterfaces()).contains(Element.class);

                if (isXmletElement){
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
        /*
        AttrRelEnumRelLinkType.validateRestrictions(EnumRelLinkType.HELP);
        AttrTargetEnumTargetBrowsingContext.validateRestrictions(EnumTargetBrowsingContext._BLANK);
        AttrFormtargetEnumFormtargetBrowsingContext.validateRestrictions(EnumFormtargetBrowsingContext._BLANK);
        AttrMediaEnumMediaMediaType.validateRestrictions(EnumMediaMediaType.ALL);
        AttrAsyncEnumAsyncScript.validateRestrictions(EnumAsyncScript.ASYNC);
        AttrCheckedEnumCheckedCommand.validateRestrictions(EnumCheckedCommand.CHECKED);
        AttrControlsEnumControlsAudio.validateRestrictions(EnumControlsAudio.CONTROLS);
        AttrControlsEnumControlsVideo.validateRestrictions(EnumControlsVideo.CONTROLS);
        AttrAutofocusEnumAutofocusButton.validateRestrictions(EnumAutofocusButton.AUTOFOCUS);
        AttrAutofocusEnumAutofocusKeygen.validateRestrictions(EnumAutofocusKeygen.AUTOFOCUS);
        AttrAutofocusEnumAutofocusSelect.validateRestrictions(EnumAutofocusSelect.AUTOFOCUS);
        AttrAutofocusEnumAutofocusTextarea.validateRestrictions(EnumAutofocusTextarea.AUTOFOCUS);
        AttrAutobufferEnumAutobufferAudio.validateRestrictions(EnumAutobufferAudio.AUTOBUFFER);
        AttrAutobufferEnumAutobufferVideo.validateRestrictions(EnumAutobufferVideo.AUTOBUFFER);
        AttrAutocompleteEnumAutocompleteForm.validateRestrictions(EnumAutocompleteForm.ON);
        AttrAutoplayEnumAutoplayAudio.validateRestrictions(EnumAutoplayAudio.AUTOPLAY);
        AttrCheckedEnumCheckedInput.validateRestrictions(EnumCheckedInput.CHECKED);
        AttrDisabledEnumDisabledCommand.validateRestrictions(EnumDisabledCommand.DISABLED);
        AttrContenteditableEnumContenteditable.validateRestrictions(EnumContenteditable.FALSE);
        AttrDeferEnumDeferScript.validateRestrictions(EnumDeferScript.DEFER);
        AttrDirEnumDir.validateRestrictions(EnumDir.RTL);
        AttrDisabledEnumDisabledButton.validateRestrictions(EnumDisabledButton.AUTOFOCUS);
        AttrDisabledEnumDisabledCommand.validateRestrictions(EnumDisabledCommand.DISABLED);
        AttrDisabledEnumDisabledInput.validateRestrictions(EnumDisabledInput.DISABLED);
        AttrDisabledEnumDisabledKeygen.validateRestrictions(EnumDisabledKeygen.DISABLED);
        AttrDisabledEnumDisabledOptgroup.validateRestrictions(EnumDisabledOptgroup.DISABLED);
        AttrDisabledEnumDisabledOption.validateRestrictions(EnumDisabledOption.DISABLED);
        AttrDisabledEnumDisabledSelect.validateRestrictions(EnumDisabledSelect.AUTOFOCUS);
        AttrDisabledEnumDisabledTextarea.validateRestrictions(EnumDisabledTextarea.DISABLED);
        AttrDraggableEnumDraggable.validateRestrictions(EnumDraggable.AUTO);
        AttrEnctypeEnumEnctypeForm.validateRestrictions(EnumEnctypeForm.MULTIPART_FORM_DATA);
        AttrAutoplayEnumAutoplayVideo.validateRestrictions(EnumAutoplayVideo.AUTOPLAY);
        AttrFormenctypeEnumFormenctypeButton.validateRestrictions(EnumFormenctypeButton.APPLICATION_X_WWW_FORM_URLENCODED);
        AttrFormenctypeEnumFormenctypeInput.validateRestrictions(EnumFormenctypeInput.APPLICATION_X_WWW_FORM_URLENCODED);
        AttrFormmethodEnumFormmethodButton.validateRestrictions(EnumFormmethodButton.DELETE);
        AttrFormmethodEnumFormmethodInput.validateRestrictions(EnumFormmethodInput.DELETE);
        AttrFormnovalidateEnumFormnovalidateButton.validateRestrictions(EnumFormnovalidateButton.FORMNOVALIDATE);
        AttrFormnovalidateEnumFormnovalidateInput.validateRestrictions(EnumFormnovalidateInput.FORMNOVALIDATE);
        AttrHiddenEnumHidden.validateRestrictions(EnumHidden.HIDDEN);
        AttrHttpEquivEnumHttpEquivMeta.validateRestrictions(EnumHttpEquivMeta.REFRESH);
        AttrIsmapEnumIsmapImg.validateRestrictions(EnumIsmapImg.ISMAP);
        AttrKeytypeEnumKeytypeKeygen.validateRestrictions(EnumKeytypeKeygen.RSA);
        AttrLoopEnumLoopAudio.validateRestrictions(EnumLoopAudio.LOOP);
        AttrLoopEnumLoopVideo.validateRestrictions(EnumLoopVideo.LOOP);
        AttrMethodEnumMethodForm.validateRestrictions(EnumMethodForm.DELETE);
        AttrMultipleEnumMultipleSelect.validateRestrictions(EnumMultipleSelect.MULTIPLE);
        AttrNameEnumNameBrowsingContext.validateRestrictions(EnumNameBrowsingContext._BLANK);
        AttrNovalidateEnumNovalidateForm.validateRestrictions(EnumNovalidateForm.NOVALIDATE);
        AttrOpenEnumOpenDetails.validateRestrictions(EnumOpenDetails.OPEN);
        AttrReadonlyEnumReadonlyTextarea.validateRestrictions(EnumReadonlyTextarea.READONLY);
        AttrRequiredEnumRequiredTextarea.validateRestrictions(EnumRequiredTextarea.REQUIRED);
        AttrReversedEnumReversedOl.validateRestrictions(EnumReversedOl.REVERSED);
        AttrRunatEnumRunat.validateRestrictions(EnumRunat.SERVER);
        AttrSandboxEnumSandboxIframe.validateRestrictions(EnumSandboxIframe.ALLOW_FORMS);
        AttrScopedEnumScopedStyle.validateRestrictions(EnumScopedStyle.SCOPED);
        AttrScopeEnumScopeTh.validateRestrictions(EnumScopeTh.COL);
        AttrSeamlessEnumSeamlessIframe.validateRestrictions(EnumSeamlessIframe.SEAMLESS);
        AttrSelectedEnumSelectedOption.validateRestrictions(EnumSelectedOption.SELECTED);
        AttrShapeEnumShapeArea.validateRestrictions(EnumShapeArea.CIRCLE);
        AttrSpellcheckEnumSpellcheck.validateRestrictions(EnumSpellcheck.FALSE);
        AttrTypeEnumTypeButton.validateRestrictions(EnumTypeButton.BUTTON);
        AttrTypeEnumTypeCommand.validateRestrictions(EnumTypeCommand.CHECKBOX);
        AttrTypeEnumTypeInput.validateRestrictions(EnumTypeInput.BUTTON);
        AttrTypeEnumTypeMenu.validateRestrictions(EnumTypeMenu.CONTEXT);
        AttrTypeEnumTypeScript.validateRestrictions(EnumTypeScript.TEXT_ECMASCRIPT);
        AttrTypeEnumTypeSimpleContentType.validateRestrictions(EnumTypeSimpleContentType.TEXT_ASA);
        AttrTypeEnumTypeStyle.validateRestrictions(EnumTypeStyle.TEXT_CSS);
        AttrWrapEnumWrapTextarea.validateRestrictions(EnumWrapTextarea.HARD);
        */
    }
}