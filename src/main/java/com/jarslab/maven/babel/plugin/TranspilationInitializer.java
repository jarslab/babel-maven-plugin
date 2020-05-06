package com.jarslab.maven.babel.plugin;

import com.jarslab.maven.babel.plugin.transpiler.ImmutableTranspilation;
import com.jarslab.maven.babel.plugin.transpiler.ImmutableTranspilationContext;
import com.jarslab.maven.babel.plugin.transpiler.Transpilation;
import com.jarslab.maven.babel.plugin.transpiler.TranspilationContext;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

class TranspilationInitializer
{
    private final BabelMojo babelMojo;

    TranspilationInitializer(final BabelMojo babelMojo)
    {
        this.babelMojo = requireNonNull(babelMojo);
    }

    Set<Transpilation> getTranspilations()
    {
        final Set<ImmutableTranspilation.Builder> transpilations = new HashSet<>();
        final TranspilationContext context = ImmutableTranspilationContext.builder()
                .babelSource(babelMojo.getBabelSrc())
                .charset(Charset.forName(babelMojo.getEncoding()))
                .log(babelMojo.getLog())
                .isVerbose(babelMojo.isVerbose())
                .presets(getFormattedPresets(babelMojo))
                .plugins(babelMojo.getPlugins())
                .build();
        addStaticFiles(transpilations);
        addPatternMatchedFiles(transpilations);
        return transpilations.stream()
                .map(transpilation -> transpilation.context(context).build())
                .collect(toSet());
    }

    private void addStaticFiles(final Set<ImmutableTranspilation.Builder> sourceFiles)
    {
        // Add statically added files
        babelMojo.getJsSourceFiles().stream()
                .map(this::removeLeadingSlash)
                .map(this::resolveAgainstSourceDirectory)
                .filter(sourcePath -> Files.exists(sourcePath))
                .map(this::toTranspilationBuilder)
                .forEach(sourceFiles::add);
    }

    private Path resolveAgainstSourceDirectory(final String sourcePath)
    {
        return babelMojo.getSourceDir().toPath().resolve(sourcePath);
    }

    private void addPatternMatchedFiles(final Set<ImmutableTranspilation.Builder> sourceFiles)
    {
        // Add pattern matched files
        if (!babelMojo.getJsSourceIncludes().isEmpty()) {
            Stream.of(getIncludesDirectoryScanner().getIncludedFiles())
                    .map(this::resolveAgainstSourceDirectory)
                    .map(this::toTranspilationBuilder)
                    .forEach(sourceFiles::add);
        }
    }

    private ImmutableTranspilation.Builder toTranspilationBuilder(final Path sourceFile)
    {
        return ImmutableTranspilation.builder()
                .source(sourceFile)
                .target(determineTargetPath(sourceFile));
    }

    private String getFormattedPresets(final BabelMojo mojo)
    {
        return Stream.of(mojo.getPresets().split(","))
                .map(String::trim)
                .map(preset -> format("'%s'", preset))
                .collect(joining(","));
    }

    private Path determineTargetPath(final Path sourceFile)
    {
        final Path relativePath = getRelativePath(sourceFile);
        final String prefix = babelMojo.getPrefix() == null ? "" : babelMojo.getPrefix();
        return babelMojo.getTargetDir().toPath().resolve(relativePath).resolve(prefix + sourceFile.getFileName());
    }

    private String removeLeadingSlash(final String subject)
    {
        if (subject.startsWith(File.separator) || subject.startsWith("/")) {
            return subject.substring(1);
        }
        return subject;
    }

    private DirectoryScanner getIncludesDirectoryScanner()
    {
        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(checkFileSeparator(babelMojo.getJsSourceIncludes()));
        scanner.setExcludes(checkFileSeparator(babelMojo.getJsSourceExcludes()));
        scanner.addDefaultExcludes();
        scanner.setBasedir(babelMojo.getSourceDir());
        scanner.scan();
        return scanner;
    }

    /**
     * If '/' is used in the given paths, and the system file separator is not
     * '/', replace '/' with the {@link File#separator}. This is required for
     * using {@link DirectoryScanner}
     */
    private String[] checkFileSeparator(final List<String> paths)
    {
        return paths.stream()
                .map(path -> {
                    if (File.separatorChar != '/' && path.contains("/")) {
                        return path.replace("/", File.separator);
                    }
                    return path;
                })
                .map(this::removeLeadingSlash)
                .toArray(String[]::new);
    }

    private Path getRelativePath(final Path sourceFile)
    {
        return Paths.get(
                babelMojo.getSourceDir()
                        .toURI()
                        .relativize(sourceFile.getParent()
                                .toFile()
                                .toURI())
                        .getPath());
    }
}