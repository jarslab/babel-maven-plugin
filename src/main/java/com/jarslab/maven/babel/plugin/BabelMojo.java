package com.jarslab.maven.babel.plugin;

import com.jarslab.maven.babel.plugin.transpiler.ParallelBabelTranspilerStrategy;
import com.jarslab.maven.babel.plugin.transpiler.Transpilation;
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
import static java.nio.charset.Charset.defaultCharset;

@Mojo(name = "babel", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true)
public class BabelMojo extends AbstractMojo
{
    @Parameter(property = "verbose", defaultValue = "false")
    private boolean verbose = false;

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

    @Parameter(property = "plugins")
    private String plugins = "";

    @Parameter(property = "encoding")
    private String encoding = defaultCharset().name();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        final Charset charset = Charset.forName(encoding);
        if (verbose) {
            getLog().info("Run in the verbose mode.");
            getLog().info(format("Charset: %s.", charset));
            getLog().debug(toString());
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

        final Set<Transpilation> transpilations = new TranspilationInitializer(this).getTranspilations();
        if (transpilations.isEmpty()) {
            getLog().info("No files found to transpile.");
            return;
        }
        if (verbose) {
            getLog().info(format("Found %s files to transpile.", transpilations.size()));
        }

        try {
            new ParallelBabelTranspilerStrategy(getLog(), threads)
                    .execute(transpilations)
                    .parallel()
                    .forEach(TargetFileWriter::writeTargetFile);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed on Babel transpile execution.", e);
        }
        getLog().info("Babel transpile execution successful.");
    }

    public boolean isVerbose()
    {
        return this.verbose;
    }

    public int getThreads()
    {
        return this.threads;
    }

    public File getBabelSrc()
    {
        return this.babelSrc;
    }

    public File getSourceDir()
    {
        return this.sourceDir;
    }

    public File getTargetDir()
    {
        return this.targetDir;
    }

    public List<String> getJsSourceFiles()
    {
        return this.jsSourceFiles;
    }

    public List<String> getJsSourceIncludes()
    {
        return this.jsSourceIncludes;
    }

    public List<String> getJsSourceExcludes()
    {
        return this.jsSourceExcludes;
    }

    public String getPrefix()
    {
        return this.prefix;
    }

    public String getPresets()
    {
        return this.presets;
    }

    public String getPlugins()
    {
        return this.plugins;
    }

    public String getEncoding()
    {
        return this.encoding;
    }

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

    public void setThreads(int threads)
    {
        this.threads = threads;
    }

    public void setBabelSrc(File babelSrc)
    {
        this.babelSrc = babelSrc;
    }

    public void setSourceDir(File sourceDir)
    {
        this.sourceDir = sourceDir;
    }

    public void setTargetDir(File targetDir)
    {
        this.targetDir = targetDir;
    }

    public void setJsSourceFiles(List<String> jsSourceFiles)
    {
        this.jsSourceFiles = jsSourceFiles;
    }

    public void setJsSourceFile(String jsSourceFile)
    {
        jsSourceFiles.add(jsSourceFile);
    }

    public void setJsSourceIncludes(List<String> jsSourceIncludes)
    {
        this.jsSourceIncludes = jsSourceIncludes;
    }

    public void setJsSourceInclude(String jsSourceInclude)
    {
        jsSourceIncludes.add(jsSourceInclude);
    }

    public void setJsSourceExcludes(List<String> jsSourceExcludes)
    {
        this.jsSourceExcludes = jsSourceExcludes;
    }

    public void setJsSourceExclude(String jsSourceExclude)
    {
        this.jsSourceExcludes.add(jsSourceExclude);
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public void setPresets(String presets)
    {
        this.presets = presets;
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    @Override
    public String toString()
    {
        return "BabelMojo{" +
                "verbose=" + verbose +
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