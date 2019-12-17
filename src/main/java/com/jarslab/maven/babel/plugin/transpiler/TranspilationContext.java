package com.jarslab.maven.babel.plugin.transpiler;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.nio.charset.Charset;

@Builder
@Data
@EqualsAndHashCode
public class TranspilationContext {

    private final File babelSource;
    private final Charset charset;
    private final Log log;
    private final boolean verbose;
    private final String presets;

}
