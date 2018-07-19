package benchmark;

import benchmark.Faster.FasterNoIndentationVisitor;
import benchmark.Faster.FasterWithIndentationVisitor;
import benchmark.Regular.RegularNoIndentationVisitor;
import benchmark.Regular.RegularWithIndentationVisitor;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Param;
import org.xmlet.htmlapi.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static j2html.TagCreator.*;

@SuppressWarnings("Duplicates")
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

    public String htmlFlow(){
        Html<Html> html = new Html<>();

        String userName = "Luís";

        html.
            body()
                .div()
                    .b()
                        .text("Hello").º()
                    .i()
                        .text(userName);

        return "";
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