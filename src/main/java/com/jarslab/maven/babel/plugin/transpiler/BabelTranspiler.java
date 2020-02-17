package com.jarslab.maven.babel.plugin.transpiler;

import org.apache.maven.plugin.logging.Log;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class BabelTranspiler implements AutoCloseable
{
    private static final String INPUT_VARIABLE = "input";
    private static final String BABEL_EXECUTE = "Babel.transform(%s, {presets: [%s]}).code";

    private TranspilationContext transpilationContext;
    private Context executionContext;

    private void initialize(final TranspilationContext context)
    {
        requireNonNull(context);
        if (this.transpilationContext == null || !this.transpilationContext.equals(context)) {
            this.transpilationContext = context;
            createEngine();
        }
    }

    private void createEngine()
    {
        transpilationContext.getLog().debug("Initializing script engine");
        try {
            executionContext = Context.newBuilder().allowExperimentalOptions(true).build();
            executionContext.eval(Source.newBuilder("js", transpilationContext.getBabelSource()).build());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public synchronized Transpilation execute(final Transpilation transpilation)
    {
        initialize(transpilation.getContext());
        final Log log = transpilationContext.getLog();
        if (transpilationContext.isVerbose()) {
            log.info(format("Transpiling %s -> %s", transpilation.getSource(), transpilation.getTarget()));
        }
        try {
            final String source = Files.lines(transpilation.getSource(), transpilationContext.getCharset())
                    .collect(joining(lineSeparator()));
            final Value bindings = executionContext.getBindings("js");
            bindings.putMember(INPUT_VARIABLE, source);
            final String result = executionContext.eval("js", format(BABEL_EXECUTE, INPUT_VARIABLE, transpilationContext.getPresets())).asString();
            if (log.isDebugEnabled()) {
                log.debug(format("%s result:\n%s", transpilation.getTarget(), result));
            }
            return ImmutableTranspilation.copyOf(transpilation).withResult(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close()
    {
        executionContext.close();
    }
}
