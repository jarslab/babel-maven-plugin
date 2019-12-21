package com.jarslab.maven.babel.plugin.transpiler;

import org.apache.maven.plugin.logging.Log;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class BabelTranspiler
{
    private static final String JAVASCRIPT_MIME_TYPE = "text/javascript";
    private static final String INPUT_VARIABLE = "input";
    private static final String BABEL_EXECUTE = "Babel.transform(%s, {presets: [%s]}).code";

    private TranspilationContext context;
    private ScriptEngine engine;
    private SimpleBindings simpleBindings;

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
        try (InputStreamReader babelReader = new InputStreamReader(
                new FileInputStream(context.getBabelSource()), context.getCharset())) {
            engine = new ScriptEngineManager(null).getEngineByMimeType(JAVASCRIPT_MIME_TYPE);
            simpleBindings = new SimpleBindings();
            engine.eval(babelReader, simpleBindings);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
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
            simpleBindings.put(INPUT_VARIABLE, source);
            final String result = (String) engine.eval(
                    format(BABEL_EXECUTE, INPUT_VARIABLE, context.getPresets()), simpleBindings);
            if (log.isDebugEnabled()) {
                log.debug(format("%s result:\n%s", transpilation.getTarget(), result));
            }
            return ImmutableTranspilation.copyOf(transpilation).withResult(result);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}
