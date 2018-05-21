package benchmark;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.xmlet.htmlapi.Body;
import org.xmlet.htmlapi.Div;
import org.xmlet.htmlapi.Html;
import org.xmlet.htmlapi.Table;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static j2html.TagCreator.*;

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
@Measurement(iterations = 8, time=1)
@Warmup(iterations = 12, time=1)
@Fork(1)
@State(Scope.Benchmark)
public class BenchmarkMain {

    @Param({"100"})//, "1000"}), "10000", "100000", "1000000"})
    private int elementCount;
    private List<String> values;
    private VelocityEngine ve;
    private Template t;
    private VelocityContext context;
    private StringWriter writer;
    private CustomBenchmarkVisitor<List<String>> customVisitor;

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
    }

    @Benchmark
    public String htmlApiBenchmarkDivs() {
        Html<Html> root = new Html<>();
        Body<Html<Html>> body = root.body();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");
        Div<Body<Html<Html>>> d1 = body.div();

        for (String value : values){
            d1 = d1.div().text(value).º().div().º();
        }

        root.accept(customVisitor);

        return customVisitor.getResult();
    }

    @Benchmark
    public String htmlApiBenchmarkTable() {
        Html<Html> root = new Html<>();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");

        values.forEach(value -> table.tr().td().text(value));

        root.accept(customVisitor);

        return customVisitor.getResult();
    }

    @Benchmark
    public String velocityBenchmark() {
        t.merge( context, writer );

        return writer.toString();
    }

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

    public static void main( String[] args ) throws Exception {
        Options opts = new OptionsBuilder()
                .include(".*")
                .warmupIterations(10)
                .measurementIterations(10)
                .jvmArgs("-Xms2g", "-Xmx2g")
                .shouldDoGC(true)
                .forks(1)
                .build();

        new Runner(opts).run();
    }
}

/*
Regular test

Benchmark                            (elementCount)   Mode  Cnt   Score   Error   Units
BenchmarkMain.htmlApiBenchmarkDivs              100  thrpt    8  24$052 ± 0$795  ops/ms
BenchmarkMain.htmlApiBenchmarkTable             100  thrpt    8  18$834 ± 0$666  ops/ms
BenchmarkMain.j2htmlBenchmark                   100  thrpt    8  15$664 ± 0$475  ops/ms
BenchmarkMain.velocityBenchmark                 100  thrpt    8   7$301 ± 0$217  ops/ms

"Emtpy" visitor

Benchmark                            (elementCount)   Mode  Cnt   Score   Error   Units
BenchmarkMain.htmlApiBenchmarkDivs              100  thrpt    8  48$157 ± 1$911  ops/ms
BenchmarkMain.htmlApiBenchmarkTable             100  thrpt    8  46$708 ± 1$452  ops/ms
*/