package benchmark;

import benchmark.Faster.FasterNoIndentationVisitor;
import benchmark.Faster.FasterWithIndentationVisitor;
import benchmark.Regular.RegularNoIndentationVisitor;
import benchmark.Regular.RegularWithIndentationVisitor;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Param;
import org.xmlet.htmlapi.*;

import java.io.*;
import java.lang.Object;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static j2html.TagCreator.*;

@SuppressWarnings("Duplicates")
@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 10, time=1)
@Measurement(iterations = 10, time=1)
@Fork(1)
@State(Scope.Benchmark)
public class BenchmarkMain<T> {

    @Param({"10", "100", "1000", "10000", "100000", "1000000"})//, "1000"}), "10000", "100000", "1000000"})
    private int elementCount;
    private List<String> values;
    private Template t;
    private VelocityContext context;
    private StringWriter writer;
    private FasterNoIndentationVisitor fasterNoIndentationVisitor;
    private FasterWithIndentationVisitor fasterWithIndentationVisitor;
    private RegularNoIndentationVisitor<List<String>> regularNoIndentationVisitor;
    private RegularWithIndentationVisitor<List<String>> regularWithIndentationVisitor;
    private String toWrite;

    @Setup
    public void setup() {
        values = new ArrayList<>();

        for (int i = 0; i < elementCount; i++) {
            values.add("val" + i);
        }

        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        ve.init();

        t = ve.getTemplate("helloworld.vm");
    }

    @Setup(value=Level.Invocation)
    public void invocationSetup(){
        // Velocity
        context = new VelocityContext();
        context.put("title", "Title");
        context.put("values", values);

        writer = new StringWriter();

        // HtmlApi
        regularNoIndentationVisitor = new RegularNoIndentationVisitor<>();
        regularWithIndentationVisitor = new RegularWithIndentationVisitor<>();

        // HtmlApiFaster
        fasterNoIndentationVisitor = new FasterNoIndentationVisitor();
        fasterWithIndentationVisitor = new FasterWithIndentationVisitor();
    }
/*
    @Benchmark
    public String htmlApiFasterDivs() {
        Html<Html> root = new Html<>(fasterWithIndentationVisitor);
        Body<Html<Html>> body = root.body();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");
        Div d1 = body.div();

        for (String value : values){
            d1 = ((Div) d1.div().text(value)).div();
        }

        return fasterWithIndentationVisitor.getResult(d1);
    }

    @Benchmark
    public String htmlApiFasterDivsNoIndentation() {
        Html<Html> root = new Html<>(fasterNoIndentationVisitor);
        Body<Html<Html>> body = root.body();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");
        Div d1 = body.div();

        for (String value : values){
            d1 = ((Div) d1.div().text(value)).div();
        }

        return fasterNoIndentationVisitor.getResult(d1);
    }

    @Benchmark
    public String htmlApiFasterTable() {
        Html<Html> root = new Html<>(fasterWithIndentationVisitor);

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title").º().º();

        for (String value : values){
            table.tr().td().text(value).º().º();
        }

        return fasterWithIndentationVisitor.getResult(table);
    }

    @Benchmark
    public String htmlApiFasterTableNoIndentation() {
        Html<Html> root = new Html<>(fasterNoIndentationVisitor);

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title").º().º();

        for (String value : values){
            table.tr().td().text(value).º().º();
        }

        return fasterNoIndentationVisitor.getResult(table);
    }
*/
    @Benchmark
    public String htmlApiDivs() {
        Html<Html> root = new Html<>();
        Body<Html<Html>> body = root.body();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");
        Div d1 = body.div();

        for (String value : values){
            d1 = ((Div) d1.div().text(value)).div();
        }

        root.accept(regularWithIndentationVisitor);
        return regularWithIndentationVisitor.getResult();
    }

    @Benchmark
    public String htmlApiDivsNoIndentation() {
        Html<Html> root = new Html<>();
        Body<Html<Html>> body = root.body();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");
        Div d1 = body.div();

        for (String value : values){
            d1 = ((Div) d1.div().text(value)).div();
        }

        root.accept(regularNoIndentationVisitor);
        return regularNoIndentationVisitor.getResult();
    }

    @Benchmark
    public String htmlApiTable() {
        Html<Html> root = new Html<>();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title").º().º();

        for (String value : values){
            table.tr().td().text(value).º().º();
        }

        root.accept(regularWithIndentationVisitor);
        return regularWithIndentationVisitor.getResult();
    }

    @Benchmark
    public String htmlApiTableNoIndentation() {
        Html<Html> root = new Html<>();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title").º().º();

        for (String value : values){
            table.tr().td().text(value).º().º();
        }

        root.accept(regularNoIndentationVisitor);
        return regularNoIndentationVisitor.getResult();
    }

    @Benchmark
    public String velocity() {
        t.merge( context, writer );

        return writer.toString();
    }

    @Benchmark
    public String j2html() {
        return document(
                html(
                        body(
                                table(
                                        tr(
                                                th("Title")
                                        ),
                                        each(values, value ->
                                                tr(
                                                        td(value)
                                                )
                                        )
                                ),
                                div()
                        )
                )
        );
    }

    private static List<TableElement> getTableElementsEx1(){
        List<TableElement> tableElements = new ArrayList<>();

        tableElements.add(new TableElement("Alfreds Futterkiste", "Maria Anders", "Germany"));
        tableElements.add(new TableElement("Centro comercial Moctezuma", "Francisco Chang", "Mexico"));
        tableElements.add(new TableElement("Ernst Handel", "Roland Mendel", "Austria"));
        tableElements.add(new TableElement("Island Trading", "Helen Bennett", "UK"));
        tableElements.add(new TableElement("Laughing Bacchus Winecellars", "Yoshi Tannamuri", "Canada"));
        tableElements.add(new TableElement("Magazzini Alimentari Riuniti", "Giovanni Rovelli", "Italy"));

        return tableElements;
    }

    Iterable<T> tableElementsEx2 = getTableElementsEx2();

    private static <T> List<T> getTableElementsEx2(){
        List<T> tableElements = new ArrayList<>();

        tableElements.add((T) new TableElement("Alfreds Futterkiste", "Maria Anders", "Germany"));
        tableElements.add((T) new TableElement("Centro comercial Moctezuma", "Francisco Chang", "Mexico"));
        tableElements.add((T) new TableElement("Ernst Handel", "Roland Mendel", "Austria"));
        tableElements.add((T) new TableElement("Island Trading", "Helen Bennett", "UK"));
        tableElements.add((T) new TableElement("Laughing Bacchus Winecellars", "Yoshi Tannamuri", "Canada"));
        tableElements.add((T) new TableElement("Magazzini Alimentari Riuniti", "Giovanni Rovelli", "Italy"));

        return tableElements;
    }

    public static void main( String[] args ) throws Exception {
        //ex1();
        ex2();
        //ex1HtmlApi();
        //ex2HtmlApi();
    }

    static void ex1() throws IOException {
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();

        Mustache mustache = mf.compile(new StringReader(
                "<table>\n" +
                        "    <tr>\n" +
                        "        <th>company</th>\n" +
                        "        <th>contact</th>\n" +
                        "        <th>country</th>\n" +
                        "    </tr>\n" +
                        "    {{#tableElements}}\n" +
                        "    <tr>\n" +
                        "        <td>{{company}}</td>\n" +
                        "        <td>{{contact}}</td>\n" +
                        "        <td>{{country}}</td>\n" +
                        "    </tr>\n" +
                        "    {{/tableElements}}" +
                        "</table>"), "helloworld");

        mustache.execute(writer, new Object(){
            Iterable<TableElement> tableElements = getTableElementsEx1();
        });
        writer.flush();
    }

    static void ex2() throws IOException {
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();

        StringBuilder templateString = new StringBuilder(
                "<table>\n" +
                "    <tr>\n" +
                "    {{#headerNames}}\n" +
                "        <th>{{.}}</th>\n" +
                "    {{/headerNames}}" +
                "    {{#data}}\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "       {{#.}}" +
                "       <td>{{.}}</td>\n" +
                "       {{/.}}" +
                "    </tr>\n" +
                "    {{/data}}\n" +
                "</table>");

        Mustache mustache = mf.compile(new StringReader(templateString.toString()), "helloworld");

        mustache.execute(writer, new Object(){
            List<String> headerNames = getNames();
            List<List<String>> data = getData();

            List<String> getNames() {
                return getFields().stream().map(Field::getName).collect(Collectors.toList());
            }

            List<List<String>> getData() {
                List<Field> fields = getFields();

                List<List<String>> rawData = new ArrayList<>();

                getTableElementsEx2()
                        .forEach(elem ->
                            rawData.add(fields.stream().map(field -> {
                                try {
                                    return field.get(elem).toString();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }

                                return "";
                            }).collect(Collectors.toList())));

                return rawData;
            }

            private List<Field> getFields() {
                List<Field> fields = new ArrayList<>();

                getTableElementsEx2().stream().findFirst().ifPresent(elem -> fields.addAll(Arrays.asList(elem.getClass().getDeclaredFields())));

                return fields;
            }
        });

        writer.flush();
    }
/*
    static <T> void ex2() throws IOException {
        Writer writer = new OutputStreamWriter(System.out);
        MustacheFactory mf = new DefaultMustacheFactory();

        List<T> elementList = BenchmarkMain.getTableElementsEx2();
        List<String> names = new ArrayList<>();

        if (!elementList.isEmpty()){
            Arrays.asList(elementList.get(0).getClass().getDeclaredFields()).forEach(field -> names.add(field.getName()));
        }

        StringBuilder templateString = new StringBuilder(
                "<table>\n" +
                        "    <tr>\n" +
                        "    {{#headerNames}}\n" +
                        "        <th>{{.}}</th>\n" +
                        "    {{/headerNames}}" +
                        "    {{#data}}\n" +
                        "    <tr>\n" +
                        "        <td>" +
                        "{{#key}}" +
                        "{{.}}" +
                        //  "{{.}}" +
                        //"{{/value}}" +
                        "{{/key}}" +
                        "</td>\n" +
                        "    </tr>\n" +
                        "    {{/data}}\n" +
                        "</table>");

        Mustache mustache = mf.compile(new StringReader(templateString.toString()), "helloworld");

        mustache.execute(writer, new Object(){
            List<String> headerNames = Arrays.asList("company", "contact", "country");
            Set<java.util.Map.Entry<T, String>> data = getData();

            Set<java.util.Map.Entry<T, String>> getData() {
                java.util.Map<T, String> map = new HashMap<>();

                getTableElementsEx2()
                        .forEach(elem -> headerNames.forEach(elem2 -> map.put((T) elem, elem2)));

                return map.entrySet();
            }
        });
        writer.flush();
    }*/

    static void ex1HtmlApi() throws IOException {
        RegularWithIndentationVisitor<List<TableElement>> visitor = new RegularWithIndentationVisitor<>(getTableElementsEx1());
        Table<Element> table = new Table<>();

        table.tr()
                .th().text("company").º()
                .th().text("contact").º()
                .th().text("country").º().º()
             .<List<TableElement>>binder((tableObj, tableElements) ->
                tableElements.forEach(tableElement ->
                    tableObj.tr()
                                .td().text(tableElement.company).º()
                                .td().text(tableElement.contact).º()
                                .td().text(tableElement.country)
                )
             );

        table.accept(visitor);
        System.out.println(visitor.getResult());
    }

    static <T> void ex2HtmlApi() throws IOException {
        List<T> elementList = getTableElementsEx2();
        List<Field> fields = elementList.isEmpty() ? new ArrayList<>() : Arrays.asList(elementList.get(0).getClass().getDeclaredFields());

        RegularWithIndentationVisitor<List<T>> visitor = new RegularWithIndentationVisitor<>(elementList);
        Table<Element> table = new Table<>();
        Tr<Table<Element>> titleRow = table.tr();

        fields.forEach(field -> titleRow.th().text(field.getName()));

        table.<List<T>>binder((tableObj, tableElements) ->
                    tableElements.forEach(tableElement -> {
                        Tr<Table<Element>> tableRow = tableObj.tr();
                        fields.forEach(field -> {
                            try {
                                tableRow.td().text(field.get(tableElement).toString());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
                    })
            );

        table.accept(visitor);
        System.out.println(visitor.getResult());
    }
}




















/**
Benchmark                                      (elementCount)   Mode  Cnt   Score   Error   Units
BenchmarkMain.htmlApiFasterDivs                           100  thrpt   10   5$245 ± 0$094  ops/ms
BenchmarkMain.htmlApiFasterDivsNoIndentation              100  thrpt   10  40$284 ± 0$656  ops/ms
BenchmarkMain.htmlApiFasterTable                          100  thrpt   10  17$465 ± 0$461  ops/ms
BenchmarkMain.htmlApiFasterTableNoIndentation             100  thrpt   10  35$680 ± 0$698  ops/ms
BenchmarkMain.j2html                                      100  thrpt   10  15$075 ± 0$151  ops/ms
BenchmarkMain.velocity                                    100  thrpt   10   6$369 ± 0$142  ops/ms


"Empty" visitor
Benchmark                         (elementCount)   Mode  Cnt    Score    Error   Units
BenchmarkMain.htmlApiFasterDivs              100  thrpt   10  445$449 ± 4$441  ops/ms
BenchmarkMain.htmlApiFasterTable             100  thrpt   10  234$206 ± 2$983  ops/ms

Tabs
Benchmark              (tabCount)   Mode  Cnt      Score     Error   Units
BenchmarkMain.charArr           2  thrpt   10  19312$733 ± 279$428  ops/ms
BenchmarkMain.old               2  thrpt   10  25144$341 ± 835$562  ops/ms

BenchmarkMain.charArr          10  thrpt   10  14306$895 ± 586$775  ops/ms
BenchmarkMain.old              10  thrpt   10   9530$141 ±  88$927  ops/ms

BenchmarkMain.charArr          25  thrpt   10   7495$481 ± 525$046  ops/ms
BenchmarkMain.old              25  thrpt   10   3640$707 ±  80$648  ops/ms

BenchmarkMain.charArr         100  thrpt   10   3537$862 ±  58$027  ops/ms
BenchmarkMain.old             100  thrpt   10   1013$398 ±  18$188  ops/ms
*/