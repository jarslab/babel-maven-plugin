package com.jarslab.maven.babel.plugin.transpiler;

import org.immutables.value.Value;

import java.nio.file.Path;
import java.util.Optional;

@Value.Immutable
public interface Transpilation {

    TranspilationContext getContext();
    Path getSource();
    Path getTarget();
    Optional<String> getResult();

}
