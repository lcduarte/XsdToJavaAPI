package benchmark;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.openjdk.jmh.annotations.*;
import org.xmlet.htmlapi.Body;
import org.xmlet.htmlapi.Div;
import org.xmlet.htmlapi.Html;
import org.xmlet.htmlapi.Table;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static j2html.TagCreator.*;

@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 10, time=1)
@Measurement(iterations = 10, time=1)
@Fork(1)
@State(Scope.Benchmark)
public class BenchmarkMain {

    @Param({"10", "100", "1000", "10000", "100000", "1000000"})//, "1000"}), "10000", "100000", "1000000"})
    private int elementCount;
    private List<String> values;
    private Template t;
    private VelocityContext context;
    private StringWriter writer;
    private CustomBenchmarkVisitor<List<String>> customVisitor;
    private NoIndentationVisitor<List<String>> noIndentationVisitor;
    private PrintWriter printWriter;
    private String toWrite;

    /*
    private StringBuilder stringBuilder;
    @Param({"2", "10", "25", "100"})
    private int tabCount;
    */

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
        customVisitor = new CustomBenchmarkVisitor<>();
        noIndentationVisitor = new NoIndentationVisitor<>();

        printWriter = new PrintWriter()
    }

    /*
    @Benchmark
    public StringBuilder old() {
        for (int i = 0; i < tabCount; ++i){
            stringBuilder.append('\t');
        }

        return stringBuilder;
    }

    @Benchmark
    public StringBuilder charArr() {
        char[] tabs = new char[tabCount];

        for (int i = 0; i < tabCount; i++) {
            tabs[i] = '\t';
        }

        return stringBuilder.append(tabs);
    }
    */

    @Benchmark
    public String htmlApiFasterDivs() {
        Html<Html> root = new Html<>(customVisitor);
        Body<Html<Html>> body = root.body();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");
        Div d1 = body.div();

        for (String value : values){
            d1 = ((Div) d1.div().text(value)).div();
        }

        return customVisitor.getResult(d1);
    }

    @Benchmark
    public String htmlApiFasterDivsNoIndentation() {
        Html<Html> root = new Html<>(noIndentationVisitor);
        Body<Html<Html>> body = root.body();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");
        Div d1 = body.div();

        for (String value : values){
            d1 = ((Div) d1.div().text(value)).div();
        }

        return noIndentationVisitor.getResult(d1);
    }

    @Benchmark
    public String htmlApiFasterTable() {
        Html<Html> root = new Html<>(customVisitor);

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title").º().º();

        for (String value : values){
            table.tr().td().text(value).º().º();
        }

        return customVisitor.getResult(table);
    }

    @Benchmark
    public String htmlApiFasterTableNoIndentation() {
        Html<Html> root = new Html<>(noIndentationVisitor);

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title").º().º();

        for (String value : values){
            table.tr().td().text(value).º().º();
        }

        return noIndentationVisitor.getResult(table);
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

    public static void main( String[] args ) throws Exception {
        List<String> values = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            values.add("val" + i);
        }

        NoIndentationVisitor customVisitor = new NoIndentationVisitor<>();

        Html<Html> root = new Html<>(customVisitor);

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().attrClass("c").th().text("Title").º().º();

        for (String value : values){
             table.tr().td().text(value).a().º().º();
        }

        System.out.println(customVisitor.getResult(table));
    }
}

/*
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