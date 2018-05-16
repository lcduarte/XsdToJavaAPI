package benchmark;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.xmlet.htmlapi.Body;
import org.xmlet.htmlapi.Html;
import org.xmlet.htmlapi.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class BenchmarkMain {

    @Param({"100", "1000", "10000", "100000", "1000000"})
    private int elementCount;
    private List<String> values;
    private VelocityEngine ve;

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
    }

    @Benchmark
    //@BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SingleShotTime})
    @BenchmarkMode({Mode.AverageTime})
    public String testHtmlApiMethod() {
        CustomBenchmarkVisitor<List<String>> customVisitor = new CustomBenchmarkVisitor<>(values);

        Html<Html> root = new Html<>();

        Table<Body<Html<Html>>> table = root.body().table();
        table.tr().th().text("Title");
        table.<List<String>>binder((elem, list) ->
                list.forEach(tdValue ->
                        elem.tr().td().text(tdValue)
                )
        ).º().div();

        root.accept(customVisitor);

        return customVisitor.getResult();
    }

    /*
    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SingleShotTime})
    public String velocityBenchmark() {
        Template t = ve.getTemplate("helloworld.vm");

        VelocityContext context = new VelocityContext();
        context.put("title", "Title");
        context.put("values", values);

        StringWriter writer = new StringWriter();

        t.merge( context, writer );

        return writer.toString();
    }
    */

    /*
    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SingleShotTime})
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
                                )
                        )
                )
        );
    }
    */

    public static void main( String[] args ) throws Exception {
        Options opts = new OptionsBuilder()
                .include(".*")
                .warmupIterations(5)
                .measurementIterations(10)
                .jvmArgs("-Xms2g", "-Xmx2g")
                .shouldDoGC(true)
                .forks(1)
                .build();

        new Runner(opts).run();
    }
}

/*
Benchmark                        (elementCount)   Mode  Cnt        Score        Error   Units
BenchmarkMain.j2htmlBenchmark               100  thrpt   10        0$019 ±      0$001  ops/us
BenchmarkMain.j2htmlBenchmark              1000  thrpt   10        0$002 ±      0$001  ops/us
BenchmarkMain.j2htmlBenchmark             10000  thrpt   10       ? 10??               ops/us
BenchmarkMain.j2htmlBenchmark            100000  thrpt   10       ? 10??               ops/us
BenchmarkMain.j2htmlBenchmark           1000000  thrpt   10       ? 10??               ops/us
BenchmarkMain.testHtmlApiMethod             100  thrpt   10       ? 10??               ops/us
BenchmarkMain.testHtmlApiMethod            1000  thrpt   10       ? 10??               ops/us
BenchmarkMain.testHtmlApiMethod           10000  thrpt   10       ? 10??               ops/us
BenchmarkMain.testHtmlApiMethod          100000  thrpt   10       ? 10??               ops/us
BenchmarkMain.testHtmlApiMethod         1000000  thrpt    4       ? 10??               ops/us
BenchmarkMain.velocityBenchmark             100  thrpt   10        0$004 ±      0$001  ops/us
BenchmarkMain.velocityBenchmark            1000  thrpt   10        0$001 ±      0$001  ops/us
BenchmarkMain.velocityBenchmark           10000  thrpt   10       ? 10??               ops/us
BenchmarkMain.velocityBenchmark          100000  thrpt   10       ? 10??               ops/us
BenchmarkMain.velocityBenchmark         1000000  thrpt   10       ? 10??               ops/us
BenchmarkMain.j2htmlBenchmark               100   avgt   10       54$025 ±      0$517   us/op
BenchmarkMain.j2htmlBenchmark              1000   avgt   10      514$585 ±      5$212   us/op
BenchmarkMain.j2htmlBenchmark             10000   avgt   10     5246$777 ±      5$145   us/op
BenchmarkMain.j2htmlBenchmark            100000   avgt   10    51338$331 ±    248$470   us/op
BenchmarkMain.j2htmlBenchmark           1000000   avgt   10  1710743$171 ±  18036$926   us/op
BenchmarkMain.testHtmlApiMethod             100   avgt   10    13009$967 ±   3548$243   us/op
BenchmarkMain.testHtmlApiMethod            1000   avgt   10    40670$336 ±  10138$225   us/op
BenchmarkMain.testHtmlApiMethod           10000   avgt   10   140219$182 ±  34126$646   us/op
BenchmarkMain.testHtmlApiMethod          100000   avgt   10   707014$448 ± 249842$116   us/op
BenchmarkMain.testHtmlApiMethod         1000000   avgt    4  7057537$367 ± 915260$740   us/op
BenchmarkMain.velocityBenchmark             100   avgt   10      243$750 ±      0$692   us/op
BenchmarkMain.velocityBenchmark            1000   avgt   10     1383$070 ±      9$076   us/op
BenchmarkMain.velocityBenchmark           10000   avgt   10    12402$962 ±    148$253   us/op
BenchmarkMain.velocityBenchmark          100000   avgt   10   128684$976 ±    302$291   us/op
BenchmarkMain.velocityBenchmark         1000000   avgt   10  1287868$479 ±  10393$400   us/op
BenchmarkMain.j2htmlBenchmark               100     ss   10      842$726 ±    305$639   us/op
BenchmarkMain.j2htmlBenchmark              1000     ss   10     2133$557 ±    297$085   us/op
BenchmarkMain.j2htmlBenchmark             10000     ss   10    12468$184 ±   5471$649   us/op
BenchmarkMain.j2htmlBenchmark            100000     ss   10    68237$151 ±   6427$954   us/op
BenchmarkMain.j2htmlBenchmark           1000000     ss   10  1624972$347 ±  53719$282   us/op
BenchmarkMain.testHtmlApiMethod             100     ss   10     7893$116 ±   4946$123   us/op
BenchmarkMain.testHtmlApiMethod            1000     ss   10    11083$901 ±   2110$805   us/op
BenchmarkMain.testHtmlApiMethod           10000     ss   10    62289$318 ±   6456$839   us/op
BenchmarkMain.testHtmlApiMethod          100000     ss   10   733217$264 ±  16597$721   us/op
BenchmarkMain.testHtmlApiMethod         1000000     ss   10  7458984$099 ± 316294$484   us/op
BenchmarkMain.velocityBenchmark             100     ss   10     5267$665 ±   1267$742   us/op
BenchmarkMain.velocityBenchmark            1000     ss   10    10199$552 ±   3616$486   us/op
BenchmarkMain.velocityBenchmark           10000     ss   10    34830$360 ±   5315$224   us/op
BenchmarkMain.velocityBenchmark          100000     ss   10   149169$362 ±   7833$645   us/op
BenchmarkMain.velocityBenchmark         1000000     ss   10  1271877$238 ±  25555$657   us/op


1º melhoria 
Benchmark                        (elementCount)  Mode  Cnt        Score       Error  Units
BenchmarkMain.testHtmlApiMethod             100  avgt   10       76$744 ±     3$700  us/op
BenchmarkMain.testHtmlApiMethod            1000  avgt   10      845$165 ±     5$236  us/op
BenchmarkMain.testHtmlApiMethod           10000  avgt   10     8683$167 ±   103$378  us/op
BenchmarkMain.testHtmlApiMethod          100000  avgt   10   187215$138 ±  3092$441  us/op
BenchmarkMain.testHtmlApiMethod         1000000  avgt   10  2663500$766 ± 20879$875  us/op

2º melhoria
Benchmark                        (elementCount)  Mode  Cnt        Score       Error  Units
BenchmarkMain.testHtmlApiMethod             100  avgt   10       82$461 ±     0$078  us/op
BenchmarkMain.testHtmlApiMethod            1000  avgt   10      700$485 ±     5$509  us/op
BenchmarkMain.testHtmlApiMethod           10000  avgt   10     8270$560 ±   109$582  us/op
BenchmarkMain.testHtmlApiMethod          100000  avgt   10   183779$750 ±  2398$402  us/op
BenchmarkMain.testHtmlApiMethod         1000000  avgt   10  2611212$063 ± 29677$103  us/op
*/