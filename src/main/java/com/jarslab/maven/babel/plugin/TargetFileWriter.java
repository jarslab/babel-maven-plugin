package com.jarslab.maven.babel.plugin;

import com.jarslab.maven.babel.plugin.transpiler.Transpilation;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

class TargetFileWriter
{
    static void writeTargetFile(final Transpilation transpilation)
    {
        final Log log = transpilation.getContext().getLog();
        final Charset charset = transpilation.getContext().getCharset();
        try {
            log.debug(String.format("writing to %s", transpilation.getTarget()));
            Files.createDirectories(transpilation.getTarget().getParent());
            final byte[] bytes = transpilation.getResult()
                    .orElseThrow(() -> new IllegalStateException(
                            "No result for transpilation. Cannot write transpilation (" + transpilation + ")"))
                    .getBytes(charset);
            Files.write(transpilation.getTarget(), bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
