package com.jarslab.maven.babel.plugin;

import org.apache.maven.plugin.logging.Log;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.util.Objects.requireNonNull;

class BabelTranspiler
{
    private static final String JAVASCRIPT_MIME_TYPE = "text/javascript";
    private static final String INPUT_VARIABLE = "input";
    private static final String BABEL_EXECUTE = "Babel.transform(%s, {presets: [%s]}).code";

    private final boolean verbose;
    private final Log log;
    private final TargetFileWriter targetFileWriter;
    private final File babelSource;
    private final Path sourceFilePath;
    private final String presets;
    private final ExecutorService executorService;

    BabelTranspiler(final boolean verbose,
                    final Log log,
                    final TargetFileWriter targetFileWriter,
                    final File babelSource,
                    final Path sourceFilePath,
                    final String presets,
                    final ExecutorService executorService)
    {
        this.verbose = verbose;
        this.log = requireNonNull(log);
        this.targetFileWriter = requireNonNull(targetFileWriter);
        this.babelSource = requireNonNull(babelSource);
        this.sourceFilePath = requireNonNull(sourceFilePath);
        this.presets = requireNonNull(presets);
        this.executorService = executorService;
    }

    void execute()
    {
        try {
            final FileReader fileReader = new FileReader(babelSource);
            final ScriptEngine engine = new ScriptEngineManager(null).getEngineByMimeType(JAVASCRIPT_MIME_TYPE);
            final SimpleBindings simpleBindings = new SimpleBindings();
            engine.eval(fileReader, simpleBindings);
            final String source = new String(Files.readAllBytes(sourceFilePath));
            if (verbose) {
                log.debug(String.format("%s source:\n%s", sourceFilePath, source));
            }
            simpleBindings.put(INPUT_VARIABLE, source);
            final String result = (String) engine.eval(String.format(BABEL_EXECUTE, INPUT_VARIABLE, presets), simpleBindings);
            if (verbose) {
                log.debug(String.format("%s result:\n%s", sourceFilePath, result));
            }
            targetFileWriter.writeTargetFile(sourceFilePath, result.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    CompletableFuture<Void> executeAsync()
    {
        requireNonNull(executorService);
        return CompletableFuture.runAsync(this::execute, executorService);
    }
}
