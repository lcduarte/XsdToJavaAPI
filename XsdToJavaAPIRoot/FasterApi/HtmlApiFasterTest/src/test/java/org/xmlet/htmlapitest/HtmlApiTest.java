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
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class HtmlApiTest {

    private static final String PACKAGE = "org.xmlet.htmlapifaster";

    @Test
    public void testGeneratedClassesIntegrity() throws Exception {
        CustomVisitor customVisitor = new CustomVisitor();

        new Html<Html>(customVisitor)
            .head()
                .comment("This is a comment.")
                .meta().attrCharset("UTF-8").__()
                .title()
                    .text("Title").__()
                .link().attrType(EnumTypeContentType.TEXT_CSS).attrHref("/assets/images/favicon.png").__()
                .link().attrType(EnumTypeContentType.TEXT_CSS).attrHref("/assets/styles/main.css").__().__()
            .body().attrClass("clear")
                .div()
                    .header()
                        .section()
                            .div()
                                .img().attrId("brand").attrSrc("./assets/images/logo.png").__()
                                .aside()
                                    .em()
                                        .text("Advertisement")
                                        .span()
                                            .text("HtmlApi is great!")
                                        .__()
                                    .__()
                                .__()
                            .__()
                        .__()
                    .__()
                .__()
            .__()
        .__();

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
                            "</html>";

        Assert.assertEquals(expected, result);
    }

    /**
     * Tests the custom regex.visitor without applying any model to text<T>
     */
    @Test
    public void testVisitsWithoutModel(){
        CustomVisitor visitor = new CustomVisitor();

        new Html<>(visitor)
                .body()
                    .div()
                        .text("This is a regular String.")
                    .__()
                .__()
            .__();

        String result = visitor.getResult();

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
        List<String> tdValues = Arrays.asList("val1", "val2", "val3");

        new Html<>(visitor)
            .body()
                .table()
                    .tr()
                        .th()
                            .text("Title")
                        .__()
                    .__()
                    .of(table ->
                        tdValues.forEach(value ->
                            table
                                .tr()
                                    .td()
                                        .text(value)
                                    .__()
                                .__()
                        )
                    )
                .__()
            .__()
        .__();

        String result = visitor.getResult();

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
        Assert.assertEquals("html", new Html<>(new CustomVisitor()).getName());
    }

    @Test
    public void testAllElements() throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        URL targetUrl = Element.class.getClassLoader().getResource("");

        Assert.assertNotNull(targetUrl);

        String target = targetUrl.getPath().replaceAll("test-", "") + "org/xmlet/htmlapifaster";

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

                if (!shouldBeTested(klass)){
                    continue;
                }

                if (implementsElement(klass)){
                    Constructor ctor1 = klass.getConstructor(ElementVisitor.class);
                    Constructor ctor2 = klass.getConstructor(Element.class);
                    Constructor ctor3 = klass.getDeclaredConstructor(Element.class, ElementVisitor.class, boolean.class);

                    Object elementInstance = ctor1.newInstance(visitor);
                    ctor2.newInstance(dummy);
                    ctor3.setAccessible(true);
                    ctor3.newInstance(dummy, visitor, true);
                    ctor3.newInstance(dummy, visitor, false);

                    Method[] methods = klass.getMethods();

                    Consumer c = (o) -> {};

                    for (Method method : methods) {
                        if (method.getParameterCount() == 0 && Element.class.isAssignableFrom(method.getReturnType())){
                            method.invoke(elementInstance);
                        }

                        if (method.getName().equals("of") || method.getName().equals("dynamic")){
                            method.invoke(elementInstance, c);
                        }

                        if (method.getParameterCount() == 1 && method.getName().startsWith("attr")){
                            Class<?> paramType = method.getParameterTypes()[0];
                            if (!paramType.isEnum()){
                                if (paramType.equals(String.class) || paramType.equals(Object.class)){
                                    method.invoke(elementInstance, "");
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
                            } else {
                                method.invoke(elementInstance, paramType.getDeclaredFields()[0].get(elementInstance));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean shouldBeTested(Class klass) {
        return  !(Modifier.isAbstract(klass.getModifiers()) || Modifier.isInterface(klass.getModifiers()) || klass.isEnum() || klass.equals(Text.class));
    }

    private boolean implementsElement(Class<?> klass) {
        List<Class<?>> interfaces = Arrays.asList(klass.getInterfaces());

        if (interfaces.contains(Element.class)){
            return true;
        } else {
            for (Class<?> anInterface : interfaces) {
                if (implementsElement(anInterface)) {
                    return true;
                }
            }
        }

        return false;
    }
}