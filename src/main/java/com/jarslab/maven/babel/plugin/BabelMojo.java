package com.jarslab.maven.babel.plugin;

import com.jarslab.maven.babel.plugin.transpiler.BabelTranspilerFactory;
import com.jarslab.maven.babel.plugin.transpiler.BabelTranspilerStrategy;
import com.jarslab.maven.babel.plugin.transpiler.Transpilation;
import com.jarslab.maven.babel.plugin.transpiler.TranspileStrategy;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@Mojo(name = "babel", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true)
public class BabelMojo extends AbstractMojo {

    @Parameter(property = "verbose", defaultValue = "false")
    private boolean verbose = false;

    /**
     * @deprecated in favor of {@link #threads}
     */
    @SuppressWarnings("DeprecatedIsStillUsed") // Still in use for backwards compatibility
    @Parameter(property = "parallel", defaultValue = "false")
    @Deprecated
    private boolean parallel = false;

    @Parameter(property = "threads", defaultValue = "1")
    private int threads = 1;

    @Parameter(property = "babelSrc", required = true)
    private File babelSrc;

    @Parameter(property = "sourceDir", required = true)
    private File sourceDir;

    @Parameter(property = "targetDir", required = true)
    private File targetDir;

    @Parameter(property = "jsSourceFiles", alias = "jsFiles")
    private List<String> jsSourceFiles = new ArrayList<>();

    @Parameter(property = "jsSourceIncludes", alias = "jsIncludes")
    private List<String> jsSourceIncludes = new ArrayList<>();

    @Parameter(property = "jsSourceExcludes", alias = "jsExcludes")
    private List<String> jsSourceExcludes = new ArrayList<>();

    @Parameter(property = "prefix")
    private String prefix;

    @Parameter(property = "presets", defaultValue = "es2015")
    private String presets;

    @Parameter(property = "encoding")
    private String encoding = Charset.defaultCharset().name();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Charset charset = Charset.forName(encoding);
        if (verbose) {
            getLog().info("Run in the verbose mode.");
            getLog().info(String.format("Charset: %s.", charset));
            getLog().debug(this.toString());
        }
        if (!babelSrc.exists() || !babelSrc.canRead()) {
            getLog().error("Given Babel file is not reachable.");
            throw new MojoFailureException("Given Babel file is not reachable.");
        }
        if (presets.isEmpty()) {
            throw new MojoFailureException("No Babel presets defined.");
        }
        if (jsSourceFiles.isEmpty() && jsSourceIncludes.isEmpty()) {
            getLog().warn("No source files provided, nothing to do.");
            return;
        }

        // For backwards compatibility, if parallel is set to true, and #threads has the default value, set the number of threads to 2
        //noinspection deprecation
        if(threads == 1 && parallel) {
            threads = 2;
        }

        TranspileStrategy transpileStrategy = threads > 1 ? TranspileStrategy.PARALLEL : TranspileStrategy.SEQUENTIAL;

        final TranspilationInitializer transpilationInitializer = new TranspilationInitializer(this);

        final Set<Transpilation> transpilations = transpilationInitializer.getTranspilations();

        if (transpilations.isEmpty()) {
            getLog().info("No files found to transpile.");
            return;
        }

        if (verbose) {
            getLog().info(format("Found %s files to transpile.", transpilations.size()));
        }

        BabelTranspilerStrategy transpiler = BabelTranspilerFactory.getTranspiler(transpileStrategy, this);

        try {
            transpiler.execute(transpilations)
                    .parallel()
                    .forEach(TargetFileWriter::writeTargetFile);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed on Babel transpile execution.", e);
        }
        getLog().info("Babel transpile execution successful.");
    }

    public boolean isVerbose() {return this.verbose;}

    @Deprecated
    public boolean isParallel() {return this.parallel;}

    public int getThreads() {return this.threads;}

    public File getBabelSrc() {return this.babelSrc;}

    public File getSourceDir() {return this.sourceDir;}

    public File getTargetDir() {return this.targetDir;}

    public List<String> getJsSourceFiles() {return this.jsSourceFiles;}

    public List<String> getJsSourceIncludes() {return this.jsSourceIncludes;}

    public List<String> getJsSourceExcludes() {return this.jsSourceExcludes;}

    public String getPrefix() {return this.prefix;}

    public String getPresets() {return this.presets;}

    public String getEncoding() {return this.encoding;}

    public void setVerbose(boolean verbose) {this.verbose = verbose; }

    @Deprecated
    public void setParallel(boolean parallel) {this.parallel = parallel; }

    public void setThreads(int threads) {this.threads = threads; }

    public void setBabelSrc(File babelSrc) {this.babelSrc = babelSrc; }

    public void setSourceDir(File sourceDir) {this.sourceDir = sourceDir; }

    public void setTargetDir(File targetDir) {this.targetDir = targetDir; }

    public void setJsSourceFiles(List<String> jsSourceFiles) {this.jsSourceFiles = jsSourceFiles; }

    public void setJsSourceFile(String jsSourceFile) {
        jsSourceFiles.add(jsSourceFile);
    }

    public void setJsSourceIncludes(List<String> jsSourceIncludes) {this.jsSourceIncludes = jsSourceIncludes; }

    public void setJsSourceInclude(String jsSourceInclude) {
        jsSourceIncludes.add(jsSourceInclude);
    }

    public void setJsSourceExcludes(List<String> jsSourceExcludes) {this.jsSourceExcludes = jsSourceExcludes; }

    public void setJsSourceExclude(String jsSourceExclude) {
        this.jsSourceExcludes.add(jsSourceExclude);
    }

    public void setPrefix(String prefix) {this.prefix = prefix; }

    public void setPresets(String presets) {this.presets = presets; }

    public void setEncoding(String encoding) {this.encoding = encoding; }

    @Override
    public String toString() {
        return "BabelMojo{" +
               "verbose=" + verbose +
               ", parallel=" + parallel +
               ", threads=" + threads +
               ", babelSrc=" + babelSrc +
               ", sourceDir=" + sourceDir +
               ", targetDir=" + targetDir +
               ", jsSourceFiles=" + jsSourceFiles +
               ", jsSourceIncludes=" + jsSourceIncludes +
               ", jsSourceExcludes=" + jsSourceExcludes +
               ", prefix='" + prefix + '\'' +
               ", presets='" + presets + '\'' +
               ", encoding='" + encoding + '\'' +
               '}';
    }

}