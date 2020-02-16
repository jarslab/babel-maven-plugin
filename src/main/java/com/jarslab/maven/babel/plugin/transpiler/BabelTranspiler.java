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

public class BabelTranspiler
{
    private static final String INPUT_VARIABLE = "input";
    private static final String BABEL_EXECUTE = "Babel.transform(%s, {presets: [%s]}).code";

    private TranspilationContext context;
    private Context engine;

    private void initialize(final TranspilationContext context)
    {
        requireNonNull(context);
        if (this.context == null || !this.context.equals(context)) {
            this.context = context;
            createEngine();
        }
    }

    private void createEngine()
    {
        context.getLog().debug("Initializing script engine");
        try {
            engine = Context.newBuilder().allowExperimentalOptions(true).build();
            engine.eval(Source.newBuilder("js", context.getBabelSource()).build());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public synchronized Transpilation execute(final Transpilation transpilation)
    {
        initialize(transpilation.getContext());
        final Log log = context.getLog();
        if (context.isVerbose()) {
            log.info(format("Transpiling %s -> %s", transpilation.getSource(), transpilation.getTarget()));
        }
        try {
            final String source = Files.lines(transpilation.getSource(), context.getCharset())
                    .collect(joining(lineSeparator()));
            final Value bindings = engine.getBindings("js");
            bindings.putMember(INPUT_VARIABLE, source);
            final String result = engine.eval("js", format(BABEL_EXECUTE, INPUT_VARIABLE, context.getPresets())).asString();
            if (log.isDebugEnabled()) {
                log.debug(format("%s result:\n%s", transpilation.getTarget(), result));
            }
            return ImmutableTranspilation.copyOf(transpilation).withResult(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
