package com.jarslab.maven.babel.plugin.transpiler;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Runs the transpilation process in parallel, creating a new BabelTranspiler
 * for each thread, and using that to execute once
 */
class ParallelBabelTranspilerStrategy implements BabelTranspilerStrategy {

    @Override
    public Stream<Transpilation> execute(Set<Transpilation> transpilations) {
        return transpilations.parallelStream()
                .map(t -> new BabelTranspiler().execute(t));
    }

}
