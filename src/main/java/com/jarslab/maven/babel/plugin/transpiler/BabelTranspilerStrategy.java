package com.jarslab.maven.babel.plugin.transpiler;

import java.util.Set;
import java.util.stream.Stream;

public interface BabelTranspilerStrategy
{
    Stream<Transpilation> execute(Set<Transpilation> transpilations);
}