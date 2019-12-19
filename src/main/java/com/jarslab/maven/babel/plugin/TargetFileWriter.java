package com.jarslab.maven.babel.plugin;

import com.jarslab.maven.babel.plugin.transpiler.Transpilation;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

class TargetFileWriter
{

    static void writeTargetFile(Transpilation transpilation)
    {
        Log log = transpilation.getContext().getLog();
        Charset charset = transpilation.getContext().getCharset();
        try {
            log.debug(String.format("writing to %s", transpilation.getTarget()));
            Files.createDirectories(transpilation.getTarget().getParent());
            Files.write(transpilation.getTarget(), transpilation.getResult().getBytes(charset));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
