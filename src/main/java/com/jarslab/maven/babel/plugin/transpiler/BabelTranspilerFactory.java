package com.jarslab.maven.babel.plugin.transpiler;

import com.jarslab.maven.babel.plugin.BabelMojo;

import static java.util.Objects.requireNonNull;

public class BabelTranspilerFactory
{
    public static BabelTranspilerStrategy getTranspiler(final TranspileStrategy transpileStrategy,
                                                        final BabelMojo babelMojo)
    {
        requireNonNull(transpileStrategy);
        requireNonNull(babelMojo);
        switch (transpileStrategy) {
            case PARALLEL:
            case SEQUENTIAL:
            default:
                return new ParallelBabelTranspilerStrategy(babelMojo.getLog(), babelMojo.getThreads());
        }
    }
}
