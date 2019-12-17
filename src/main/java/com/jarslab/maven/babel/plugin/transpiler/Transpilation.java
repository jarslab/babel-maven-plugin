package com.jarslab.maven.babel.plugin.transpiler;

import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;

@Data
@Builder
public class Transpilation {

    private TranspilationContext context;
    private final Path source;
    private final Path target;
    private String result;

}
