package com.jarslab.maven.babel.plugin.transpiler;

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Objects;

public class TranspilationContext {

    private final File babelSource;
    private final Charset charset;
    private final Log log;
    private final boolean verbose;
    private final String presets;

    private TranspilationContext(File babelSource, Charset charset, Log log, boolean verbose, String presets) {
        this.babelSource = babelSource;
        this.charset = charset;
        this.log = log;
        this.verbose = verbose;
        this.presets = presets;
    }

    public static TranspilationContextBuilder builder() {return new TranspilationContextBuilder();}

    public File getBabelSource() {
        return babelSource;
    }

    public Charset getCharset() {
        return charset;
    }

    public Log getLog() {
        return log;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public String getPresets() {
        return presets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TranspilationContext that = (TranspilationContext)o;
        return verbose == that.verbose &&
               Objects.equals(babelSource, that.babelSource) &&
               Objects.equals(charset, that.charset) &&
               Objects.equals(log, that.log) &&
               Objects.equals(presets, that.presets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(babelSource, charset, log, verbose, presets);
    }

    @Override
    public String toString() {
        return "TranspilationContext{" +
               "babelSource=" + babelSource +
               ", charset=" + charset +
               ", log=" + log +
               ", verbose=" + verbose +
               ", presets='" + presets + '\'' +
               '}';
    }

    public static class TranspilationContextBuilder {

        private File babelSource;
        private Charset charset;
        private Log log;
        private boolean verbose;
        private String presets;

        TranspilationContextBuilder() {}

        public TranspilationContext.TranspilationContextBuilder babelSource(File babelSource) {
            this.babelSource = babelSource;
            return this;
        }

        public TranspilationContext.TranspilationContextBuilder charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public TranspilationContext.TranspilationContextBuilder log(Log log) {
            this.log = log;
            return this;
        }

        public TranspilationContext.TranspilationContextBuilder verbose(boolean verbose) {
            this.verbose = verbose;
            return this;
        }

        public TranspilationContext.TranspilationContextBuilder presets(String presets) {
            this.presets = presets;
            return this;
        }

        public TranspilationContext build() {
            return new TranspilationContext(babelSource, charset, log, verbose, presets);
        }

        @Override
        public String toString() {
            return "TranspilationContextBuilder{" +
                   "babelSource=" + babelSource +
                   ", charset=" + charset +
                   ", log=" + log +
                   ", verbose=" + verbose +
                   ", presets='" + presets + '\'' +
                   '}';
        }

    }

}
