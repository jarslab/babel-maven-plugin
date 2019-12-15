package com.jarslab.maven.babel.plugin;

import lombok.RequiredArgsConstructor;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

@RequiredArgsConstructor
class TargetFileWriter
{
    private final Charset charset;
    private final Log log;

    void writeTargetFile(TranspileContext context)
    {
        try {
            log.debug(String.format("writing to %s", context.getTarget()));
            Files.createDirectories(context.getTarget().getParent());
            Files.write(context.getTarget(), context.getResult().getBytes(charset));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
