package com.jarslab.maven.babel.plugin;

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

import static java.util.Objects.requireNonNull;

class BabelTranspiler
{
    private static final String JAVASCRIPT_MIME_TYPE = "text/javascript";
    private static final String INPUT_VARIABLE = "input";
    private static final String BABEL_EXECUTE = "Babel.transform(%s, {presets: [%s]}).code";

    private final TargetFileWriter targetFileWriter;
    private final File babelSource;
    private final Path sourceFilePath;
    private final String presets;

    BabelTranspiler(final TargetFileWriter targetFileWriter,
                    final File babelSource,
                    final Path sourceFilePath,
                    final String presets)
    {
        this.targetFileWriter = requireNonNull(targetFileWriter);
        this.babelSource = requireNonNull(babelSource);
        this.sourceFilePath = requireNonNull(sourceFilePath);
        this.presets = requireNonNull(presets);
    }

    void execute()
    {
        try {
            final FileReader fileReader = new FileReader(babelSource);
            final ScriptEngine engine = new ScriptEngineManager(null).getEngineByMimeType(JAVASCRIPT_MIME_TYPE);
            final SimpleBindings simpleBindings = new SimpleBindings();
            engine.eval(fileReader, simpleBindings);
            simpleBindings.put(INPUT_VARIABLE, new String(Files.readAllBytes(sourceFilePath)));
            final String result = (String) engine.eval(String.format(BABEL_EXECUTE, INPUT_VARIABLE, presets), simpleBindings);
            targetFileWriter.writeTargetFile(sourceFilePath, result.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}
