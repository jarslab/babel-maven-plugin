package com.jarslab.maven.babel.plugin.transpiler;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Creates a single BabelTranspiler, and executes each transpilation in sequence
 * against that transpiler.
 */
class SequentialBabelTranspilerStrategy implements BabelTranspilerStrategy {

    @Override
    public Stream<Transpilation> execute(Set<Transpilation> transpilations) {
        BabelTranspiler transpiler = new BabelTranspiler();
        return transpilations.parallelStream()
                .map(transpiler::execute);
    }

}
