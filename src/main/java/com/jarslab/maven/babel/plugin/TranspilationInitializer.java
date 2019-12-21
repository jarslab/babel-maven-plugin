package com.jarslab.maven.babel.plugin;

import com.jarslab.maven.babel.plugin.transpiler.ImmutableTranspilation;
import com.jarslab.maven.babel.plugin.transpiler.ImmutableTranspilationContext;
import com.jarslab.maven.babel.plugin.transpiler.Transpilation;
import com.jarslab.maven.babel.plugin.transpiler.TranspilationContext;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

class TranspilationInitializer {

    private final BabelMojo babelMojo;

    TranspilationInitializer(BabelMojo babelMojo) {
        this.babelMojo = babelMojo;
    }

    Set<Transpilation> getTranspilations() {
        final Set<ImmutableTranspilation.Builder> transpilations = new HashSet<>();

        TranspilationContext context = ImmutableTranspilationContext.builder()
                .babelSource(babelMojo.getBabelSrc())
                .charset(Charset.forName(babelMojo.getEncoding()))
                .log(babelMojo.getLog())
                .isVerbose(babelMojo.isVerbose())
                .presets(getFormattedPresets(babelMojo))
                .build();

        addStaticFiles(transpilations);
        addPatternMatchedFiles(transpilations);

        return transpilations.stream()
            .map(t -> t.context(context).build())
            .collect(Collectors.toSet());
    }

    private void addStaticFiles(Set<ImmutableTranspilation.Builder> sourceFiles) {
        // Add statically added files
        babelMojo.getJsSourceFiles().stream()
                .map(this::removeLeadingSlash)
                .map(this::resolveAgainstSourceDirectory)
                .filter(p -> p.toFile().exists())
                .map(this::toTranspilationBuilder)
                .forEach(sourceFiles::add);
    }

    private Path resolveAgainstSourceDirectory(String s) {
        return babelMojo.getSourceDir().toPath().resolve(s);
    }

    private void addPatternMatchedFiles(Set<ImmutableTranspilation.Builder> sourceFiles) {
        // Add pattern matched files
        if (!babelMojo.getJsSourceIncludes().isEmpty()) {
            Stream.of(getIncludesDirectoryScanner().getIncludedFiles())
                    .map(this::resolveAgainstSourceDirectory)
                    .map(this::toTranspilationBuilder)
                    .forEach(sourceFiles::add);
        }
    }

    private ImmutableTranspilation.Builder toTranspilationBuilder(Path sourceFile) {
        return ImmutableTranspilation.builder()
                .source(sourceFile)
                .target(determineTargetPath(sourceFile));
    }

    private String getFormattedPresets(final BabelMojo mojo) {
        return Stream.of(mojo.getPresets().split(","))
                .map(String::trim)
                .map(preset -> format("'%s'", preset))
                .collect(Collectors.joining(","));
    }


    private Path determineTargetPath(Path sourceFile) {
        Path relativePath = getRelativePath(sourceFile);
        String prefix = babelMojo.getPrefix() == null ? "" : babelMojo.getPrefix();
        return babelMojo.getTargetDir().toPath().resolve(relativePath).resolve(prefix + sourceFile.getFileName());
    }

    private String removeLeadingSlash(String subject) {
        if (subject.startsWith(File.separator) || subject.startsWith("/")) {
            return subject.substring(1);
        }
        return subject;
    }

    private DirectoryScanner getIncludesDirectoryScanner() {
        DirectoryScanner scanner = new DirectoryScanner();

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
    private String[] checkFileSeparator(List<String> paths) {
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

    private Path getRelativePath(Path sourceFile) {
        return Paths.get(
                babelMojo.getSourceDir()
                        .toURI()
                        .relativize(sourceFile.getParent()
                                .toFile()
                                .toURI())
                        .getPath());
    }

}
