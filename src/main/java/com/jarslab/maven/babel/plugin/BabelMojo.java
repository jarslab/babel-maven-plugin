package com.jarslab.maven.babel.plugin;

import com.jarslab.maven.babel.plugin.transpiler.BabelTranspilerFactory;
import com.jarslab.maven.babel.plugin.transpiler.BabelTranspilerStrategy;
import com.jarslab.maven.babel.plugin.transpiler.Transpilation;
import com.jarslab.maven.babel.plugin.transpiler.TranspileStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@Mojo(name = "babel", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true)
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BabelMojo extends AbstractMojo {

    @Builder.Default
    @Parameter(property = "verbose", defaultValue = "false")
    private boolean verbose = false;

    @Builder.Default
    @Parameter(property = "parallel", defaultValue = "true")
    private boolean parallel = true;

    @Parameter(property = "transpileStrategy", alias = "strategy")
    private TranspileStrategy transpileStrategy;

    @Parameter(property = "babelSrc", required = true)
    private File babelSrc;

    @Parameter(property = "sourceDir", required = true)
    private File sourceDir;

    @Parameter(property = "targetDir", required = true)
    private File targetDir;

    @Singular
    @Parameter(property = "jsSourceFiles", alias = "jsFiles")
    private List<String> jsSourceFiles;

    @Singular
    @Parameter(property = "jsSourceIncludes", alias = "jsIncludes")
    private List<String> jsSourceIncludes;

    @Singular
    @Parameter(property = "jsSourceExcludes", alias = "jsExcludes")
    private List<String> jsSourceExcludes;

    @Parameter(property = "prefix")
    private String prefix;

    @Parameter(property = "presets", defaultValue = "es2015")
    private String presets;

    @Builder.Default
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

        if (transpileStrategy == null) {
            transpileStrategy = parallel ? TranspileStrategy.PARALLEL : TranspileStrategy.SEQUENTIAL;
        }

        final TranspilationInitializer transpilationInitializer = new TranspilationInitializer(this);

        final Set<Transpilation> transpilations = transpilationInitializer.getTranspilations();

        if (transpilations.isEmpty()) {
            getLog().info("No files found to transpile.");
            return;
        }

        if (verbose) {
            getLog().info(format("Found %s files to transpile.", transpilations.size()));
        }

        BabelTranspilerStrategy transpiler = BabelTranspilerFactory.getTranspiler(transpileStrategy);

        try {
            transpiler.execute(transpilations)
                    .forEach(TargetFileWriter::writeTargetFile);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed on Babel transpile execution.", e);
        }
        getLog().info("Babel transpile execution successful.");
    }

}