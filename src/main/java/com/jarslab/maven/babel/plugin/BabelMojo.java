package com.jarslab.maven.babel.plugin;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

@Mojo(name = "babel", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class BabelMojo extends AbstractMojo {

    @Parameter(property = "verbose", defaultValue = "false")
    private boolean verbose = false;
    @Parameter(property = "parallel", defaultValue = "true")
    private boolean parallel = true;
    @Parameter(property = "babelSrc", required = true)
    private File babelSrc;
    @Parameter(property = "sourceDir", required = true)
    private File sourceDir;
    @Parameter(property = "targetDir", required = true)
    private File targetDir;
    @Parameter(property = "jsSourceFiles", alias = "jsFiles")
    private List<String> jsSourceFiles;
    @Parameter(property = "jsSourceIncludes", alias = "jsIncludes")
    private List<String> jsSourceIncludes;
    @Parameter(property = "jsSourceExcludes", alias = "jsExcludes")
    private List<String> jsSourceExcludes;
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

        final TranspilationInitializer transpilationInitializer = TranspilationInitializer.builder()
                .sourceDirectory(sourceDir.toPath())
                .targetDirectory(targetDir.toPath())
                .prefix(prefix)
                .files(jsSourceFiles)
                .includes(jsSourceIncludes)
                .excludes(jsSourceExcludes)
                .build();

        final Set<TranspileContext> transpilations = transpilationInitializer.getTranspilations();

        if (transpilations.isEmpty()) {
            getLog().info("No files found to transpile.");
            return;
        }

        if (verbose) {
            getLog().info(format("Found %s files to transpile.", transpilations.size()));
        }

        final TargetFileWriter targetFileWriter = new TargetFileWriter(charset, getLog());

        BabelTranspiler transpiler = BabelTranspiler.builder()
                .verbose(verbose)
                .log(getLog())
                .babelSource(babelSrc)
                .presets(getFormattedPresets(presets))
                .charset(charset)
                .build();

        try {
            transpilations.parallelStream()
                    .map(transpiler::execute)
                    .forEach(targetFileWriter::writeTargetFile);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed on Babel transpile execution.", e);
        }
        getLog().info("Babel transpile execution successful.");
    }

    private String getFormattedPresets(final String presets) {
        return Stream.of(presets.split(","))
                .map(String::trim)
                .map(preset -> format("'%s'", preset))
                .collect(Collectors.joining(","));
    }

}