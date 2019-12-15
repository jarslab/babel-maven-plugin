package com.jarslab.maven.babel.plugin;

import lombok.Builder;
import lombok.Data;

import java.nio.file.Path;

@Data
@Builder
class TranspileContext {

    private final Path source;
    private final Path target;
    private String result;

}
