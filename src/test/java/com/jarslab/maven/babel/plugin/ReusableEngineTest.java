package com.jarslab.maven.babel.plugin;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ReusableEngineTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String EXPECTED_RESULT = "\"use strict\";\n" +
            "\n" +
            "var x = function x(_x, y) {\n" +
            "  return _x * y;\n" +
            "};";
    private Log log = new SystemStreamLog();

    @Test
    public void testBabel() throws Exception
    {
        Stopwatch stopwatch = new Stopwatch();
        InputStream inputStream = ReusableEngineTest.class.getResourceAsStream("/babel-7.8.4.min.js");
        InputStreamReader fileReader = new InputStreamReader(inputStream, UTF_8);
        final Context engine = Context.newBuilder().allowExperimentalOptions(true).build();
        engine.eval(Source.newBuilder("js", fileReader, "babel-7.8.4.min.js").build());

        log.info("Initialized script engine in " + stopwatch.stop());

        stopwatch.start();
        String source = new BufferedReader(new InputStreamReader(
                ReusableEngineTest.class.getResourceAsStream("/src/test.js")))
                .lines().collect(Collectors.joining());
        String srcBinding = "transpilationSource";
        final Value bindings = engine.getBindings("js");
        bindings.putMember(srcBinding, source);
        String result = engine.eval("js", "Babel.transform(" + srcBinding + ", { presets: ['es2015'] }).code").asString();

        stopwatch.stop();
        log.info("Transpiled source in " + stopwatch);
        log.info("Source:");
        log.info(source);
        log.info("Result:");
        log.info(result);

        assertThat(result).isEqualTo(EXPECTED_RESULT);
    }

    @Test
    /*
     * This test shows that re-using an engine to transpile multiple sources in parallel is
     * not a good idea. When using a multithreaded executor {@link Executors#newFixedThreadPool(int)},
     * errors start to occur while transpiling.
     *
     * However, because loading the babel library into the engine takes quite some time, it is a good
     * idea to eval the minified babel once, and then reuse the engine to transpile each source file.
     */
    public void testReuseEngine() throws Exception
    {

        log.info("Initialize script engine ...");

        Stopwatch stopwatch = new Stopwatch();
        InputStream inputStream = ReusableEngineTest.class.getResourceAsStream("/babel-7.8.4.min.js");
        InputStreamReader fileReader = new InputStreamReader(inputStream, UTF_8);
        final Context engine = Context.newBuilder().allowExperimentalOptions(true).build();
        engine.eval(Source.newBuilder("js", fileReader, "babel-7.8.4.min.js").build());

        log.info("Initialized script engine in " + stopwatch.stop());

        final List<Exception> exc = new ArrayList<>();
        Runnable task = () -> {

            Stopwatch sw = new Stopwatch();
            try {

                String source = new BufferedReader(new InputStreamReader(
                        ReusableEngineTest.class.getResourceAsStream("/src/test.js")))
                        .lines().collect(Collectors.joining());
                String srcBinding = "transpilationSrc";
                String optBinding = "transpilationOpt";
                final Value bindings = engine.getBindings("js");
                bindings.putMember(srcBinding, source);
                bindings.putMember(optBinding, "{ presets: ['es2015'] }");
                String result = engine.eval("js", "Babel.transform(" + srcBinding + ", { presets: ['es2015'] }).code").asString();

                assertThat(result).isEqualTo(EXPECTED_RESULT);

            } catch (Exception e) {
                exc.add(e);
            }
        };

        stopwatch.start();
        int n = 100;
        log.info(format("Transpiling %d sources ...", n));
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        for (int i = 0; i < n; i++) {
            executorService.submit(task);
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        log.info(format("Transpiled %d sources in %s", n, stopwatch.stop()));

        assertThat(executorService.isTerminated()).isTrue();

        if (!exc.isEmpty()) {
            log.error(format("%d exceptions occurred during multithreaded transpilation.", exc.size()));
            throw exc.get(0);
        }
    }

    static class Stopwatch
    {

        private DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);

        {
            DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
            decimalFormatSymbols.setGroupingSeparator(' ');
            decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        }

        private long start;
        private long end;

        public Stopwatch()
        {
            start();
        }

        public void start()
        {
            start = System.nanoTime();
        }

        Stopwatch stop()
        {
            end = System.nanoTime();
            return this;
        }

        public String toString()
        {
            return format("%sms", decimalFormat.format((end - start) / 1000000));
        }

    }
}
