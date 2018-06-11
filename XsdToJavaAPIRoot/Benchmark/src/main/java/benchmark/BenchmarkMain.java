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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static j2html.TagCreator.*;

@OutputTimeUnit(TimeUnit.SECONDS)
@BenchmarkMode(Mode.Throughput)
@Measurement(iterations = 8, time=1)
@Warmup(iterations = 12, time=1)
@Fork(1)
@State(Scope.Benchmark)
public class BenchmarkMain {

    @Param({"10", "100", "1000", "10000"})//, "1000"}), "10000", "100000", "1000000"})
    private int elementCount;
    private List<String> values;
    private VelocityEngine ve;
    private Template t;
    private VelocityContext context;
    private StringWriter writer;
    private CustomBenchmarkVisitor<List<String>> customVisitor;
    private NoIndentationVisitor<List<String>> noIndentationVisitor;

    @Setup
    public void setup() {
        values = new ArrayList<>();

        for (int i = 0; i < elementCount; i++) {
            values.add("val" + i);
        }

        ve = new VelocityEngine();
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
    }
/*
    @Benchmark
    public String htmlApiBenchmarkDivs() {
        Html<Html> root = new Html<>();
        Body<Html<Html>> body = root.body();

        Table<Body<Html<Html>>> table = body.table();
        table.tr().th().text("Title");
        Div d1 = body.div();

        for (String value : values){
            d1 = ((Div) d1.div().text(value)).div();
        }

        root.accept(customVisitor);

        return customVisitor.getResult();
    }

    @Benchmark
    public String htmlApiBenchmarkDivsNoIndentation() {
        Html<Html> root = new Html<>();
        Body<Html<Html>> body = root.body();

        Table<Body<Html<Html>>> table = body.table();
        table.tr().th().text("Title");
        Div d1 = body.div();

        for (String value : values){
            d1 = ((Div) d1.div().text(value)).div();
        }

        root.accept(noIndentationVisitor);

        return noIndentationVisitor.getResult();
    }
*/
    @Benchmark
    public String htmlApiBenchmarkTable() {
        Html<Html> root = new Html<>();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");

        values.forEach(value -> table.tr().td().text(value));

        root.accept(customVisitor);

        return customVisitor.getResult();
    }
/*
    @Benchmark
    public String htmlApiBenchmarkTableNoIndentation() {
        Html<Html> root = new Html<>();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");

        values.forEach(value -> table.tr().td().text(value));

        root.accept(noIndentationVisitor);

        return noIndentationVisitor.getResult();
    }
*/

    @Benchmark
    public String velocityBenchmark() {
        t.merge( context, writer );

        return writer.toString();
    }
/*
    @Benchmark
    public String j2htmlBenchmark() {
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
*/
    public static void main( String[] args ) throws Exception {
        List<String> values = new ArrayList<>();
        NoIndentationVisitor<List<String>> customVisitor = new NoIndentationVisitor<>();

        for (int i = 0; i < 100; i++) {
            values.add("val" + i);
        }

        Html<Html> root = new Html<>();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");

        values.forEach(value -> table.tr().td().text(value));

        root.accept(customVisitor);

        System.out.println(customVisitor.getResult());
    }
}

/*
Regular test

Benchmark                                         (elementCount)   Mode  Cnt   Score   Error   Units
BenchmarkMain.htmlApiBenchmarkDivs                           100  thrpt    8   4$089 ± 0$069  ops/ms
BenchmarkMain.htmlApiBenchmarkDivsNoIndentation              100  thrpt    8  19$241 ± 0$290  ops/ms
BenchmarkMain.htmlApiBenchmarkTable                          100  thrpt    8  13$876 ± 0$076  ops/ms
BenchmarkMain.htmlApiBenchmarkTableNoIndentation             100  thrpt    8  18$460 ± 0$215  ops/ms
BenchmarkMain.j2htmlBenchmark                                100  thrpt    8  15$666 ± 1$682  ops/ms
BenchmarkMain.velocityBenchmark                              100  thrpt    8   6$357 ± 0$174  ops/ms

"Emtpy" visitor

Benchmark                            (elementCount)   Mode  Cnt   Score   Error   Units
BenchmarkMain.htmlApiBenchmarkDivs              100  thrpt    8  48$157 ± 1$911  ops/ms
BenchmarkMain.htmlApiBenchmarkTable             100  thrpt    8  46$708 ± 1$452  ops/ms
*/