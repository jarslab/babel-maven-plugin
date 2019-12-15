package com.jarslab.maven.babel.plugin;

import lombok.Builder;
import org.apache.maven.plugin.logging.Log;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static java.lang.String.format;

@Builder
class BabelTranspiler {

    private static final String JAVASCRIPT_MIME_TYPE = "text/javascript";
    private static final String INPUT_VARIABLE = "input";
    private static final String BABEL_EXECUTE = "Babel.transform(%s, {presets: [%s]}).code";

    private final boolean verbose;
    private final Log log;
    private final String presets;
    private final Charset charset;
    private final File babelSource;
    private ScriptEngine engine;

    TranspileContext execute(TranspileContext context) {
        if (verbose) {
            log.info(format("Transpiling (%s) %s -> %s", charset, context.getSource(), context.getTarget()));
        }
        try {
            final InputStreamReader fileReader = new InputStreamReader(
                    new FileInputStream(babelSource), charset);
            final ScriptEngine engine = new ScriptEngineManager(null)
                    .getEngineByMimeType(JAVASCRIPT_MIME_TYPE);
            final SimpleBindings simpleBindings = new SimpleBindings();
            engine.eval(fileReader, simpleBindings);
            final String source = new String(Files.readAllBytes(context.getSource()), charset);
            simpleBindings.put(INPUT_VARIABLE, source);
            final String result = (String) engine.eval(
                    format(BABEL_EXECUTE, INPUT_VARIABLE, presets), simpleBindings);
            if (verbose) {
                log.debug(format("%s result:\n%s", context.getTarget(), result));
            }
            context.setResult(result);
            return context;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

}
