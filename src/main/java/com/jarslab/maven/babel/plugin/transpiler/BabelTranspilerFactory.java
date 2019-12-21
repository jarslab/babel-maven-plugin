package com.jarslab.maven.babel.plugin.transpiler;

import com.jarslab.maven.babel.plugin.BabelMojo;

public class BabelTranspilerFactory {

    public static BabelTranspilerStrategy getTranspiler(TranspileStrategy stragety, BabelMojo babelMojo){
        switch(stragety) {
            case PARALLEL:
            case SEQUENTIAL:
            default:
                return new ParallelBabelTranspilerStrategy(babelMojo.getThreads());
        }
    }

}
