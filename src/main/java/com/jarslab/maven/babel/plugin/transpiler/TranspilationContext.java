package com.jarslab.maven.babel.plugin.transpiler;

import org.apache.maven.plugin.logging.Log;
import org.immutables.value.Value;

import java.io.File;
import java.nio.charset.Charset;

@Value.Immutable
public interface TranspilationContext
{
    File getBabelSource();

    Charset getCharset();

    Log getLog();

    @Value.Default
    default boolean isVerbose()
    {
        return false;
    }

    String getPresets();

    @Value.Default
    default String getPlugins()
    {
        return "";
    }
}
