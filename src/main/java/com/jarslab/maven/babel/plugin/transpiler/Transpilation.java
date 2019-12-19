package com.jarslab.maven.babel.plugin.transpiler;

import java.nio.file.Path;

public class Transpilation {

    private TranspilationContext context;
    private final Path source;
    private final Path target;
    private String result;

    private Transpilation(TranspilationContext context, Path source, Path target, String result) {
        this.context = context;
        this.source = source;
        this.target = target;
        this.result = result;
    }

    public static TranspilationBuilder builder() {return new TranspilationBuilder();}

    public TranspilationContext getContext() {return this.context;}

    public Path getSource() {return this.source;}

    public Path getTarget() {return this.target;}

    public String getResult() {return this.result;}

    public void setContext(TranspilationContext context) {this.context = context; }

    public void setResult(String result) {this.result = result; }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Transpilation)) {
            return false;
        }
        final Transpilation other = (Transpilation)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        final Object this$context = this.getContext();
        final Object other$context = other.getContext();
        if (this$context == null ? other$context != null : !this$context.equals(other$context)) {
            return false;
        }
        final Object this$source = this.getSource();
        final Object other$source = other.getSource();
        if (this$source == null ? other$source != null : !this$source.equals(other$source)) {
            return false;
        }
        final Object this$target = this.getTarget();
        final Object other$target = other.getTarget();
        if (this$target == null ? other$target != null : !this$target.equals(other$target)) {
            return false;
        }
        final Object this$result = this.getResult();
        final Object other$result = other.getResult();
        if (this$result == null ? other$result != null : !this$result.equals(other$result)) {
            return false;
        }
        return true;
    }

    private boolean canEqual(final Object other) {return other instanceof Transpilation;}

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $context = this.getContext();
        result = result * PRIME + ($context == null ? 43 : $context.hashCode());
        final Object $source = this.getSource();
        result = result * PRIME + ($source == null ? 43 : $source.hashCode());
        final Object $target = this.getTarget();
        result = result * PRIME + ($target == null ? 43 : $target.hashCode());
        final Object $result = this.getResult();
        result = result * PRIME + ($result == null ? 43 : $result.hashCode());
        return result;
    }

    public String toString() {return "Transpilation(context=" + this.getContext() + ", source=" + this.getSource() + ", target=" + this.getTarget() + ", result=" + this.getResult() + ")";}

    public static class TranspilationBuilder {

        private TranspilationContext context;
        private Path source;
        private Path target;
        private String result;

        TranspilationBuilder() {}

        public Transpilation.TranspilationBuilder context(TranspilationContext context) {
            this.context = context;
            return this;
        }

        public Transpilation.TranspilationBuilder source(Path source) {
            this.source = source;
            return this;
        }

        public Transpilation.TranspilationBuilder target(Path target) {
            this.target = target;
            return this;
        }

        public Transpilation.TranspilationBuilder result(String result) {
            this.result = result;
            return this;
        }

        public Transpilation build() {
            return new Transpilation(context, source, target, result);
        }

        public String toString() {return "Transpilation.TranspilationBuilder(context=" + this.context + ", source=" + this.source + ", target=" + this.target + ", result=" + this.result + ")";}

    }

}
