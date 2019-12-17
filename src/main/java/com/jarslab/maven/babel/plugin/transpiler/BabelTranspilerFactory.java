package com.jarslab.maven.babel.plugin.transpiler;

public class BabelTranspilerFactory {

    public static BabelTranspilerStrategy getTranspiler(TranspileStrategy stragety){
        switch(stragety) {
            case MIXED:
                return new MixedBabelTranspilerStrategy();
            case SEQUENTIAL:
                return new SequentialBabelTranspilerStrategy();
            case PARALLEL:
            default:
                return new ParallelBabelTranspilerStrategy();
        }
    }

}
