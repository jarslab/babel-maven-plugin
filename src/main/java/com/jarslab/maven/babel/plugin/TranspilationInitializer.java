package com.jarslab.maven.babel.plugin;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Builder
class TranspilationInitializer {

    @NonNull
    private Path sourceDirectory;

    @NonNull
    private Path targetDirectory;

    @SuppressWarnings("UnusedAssignment")
    @Builder.Default
    private String prefix = "";

    @Singular("file")
    private List<String> files;

    @Singular("include")
    private List<String> includes;

    @Singular("exclude")
    private List<String> excludes;

    Set<TranspileContext> getTranspilations() {
        final Set<TranspileContext> sourceFiles = new HashSet<>();

        addStaticFiles(sourceFiles);
        addPatternMatchedFiles(sourceFiles);

        return sourceFiles;
    }

    private void addStaticFiles(Set<TranspileContext> sourceFiles) {
        // Add statically added files
        files.stream()
                .map(this::removeLeadingSlash)
                .map(sourceDirectory::resolve)
                .filter(p -> p.toFile().exists())
                .map(this::toTranspileContext)
                .forEach(sourceFiles::add);
    }

    private void addPatternMatchedFiles(Set<TranspileContext> sourceFiles) {
        // Add pattern matched files
        if (!includes.isEmpty()) {
            Stream.of(getIncludesDirectoryScanner().getIncludedFiles())
                    .map(sourceDirectory::resolve)
                    .map(this::toTranspileContext)
                    .forEach(sourceFiles::add);
        }
    }

    private TranspileContext toTranspileContext(Path sourceFile) {
        return TranspileContext.builder()
                .source(sourceFile)
                .target(determineTargetPath(sourceFile))
                .build();
    }

    private Path determineTargetPath(Path sourceFile) {
        Path relativePath = getRelativePath(sourceFile);
        return targetDirectory.resolve(relativePath).resolve(prefix + sourceFile.getFileName());
    }


    private String removeLeadingSlash(String subject) {
        if (subject.startsWith(File.separator) || subject.startsWith("/")) {
            return subject.substring(1);
        }
        return subject;
    }

    private DirectoryScanner getIncludesDirectoryScanner() {
        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setIncludes(checkFileSeparator(this.includes));
        scanner.setExcludes(checkFileSeparator(this.excludes));
        scanner.addDefaultExcludes();
        scanner.setBasedir(sourceDirectory.toFile());
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
                sourceDirectory.toFile()
                        .toURI()
                        .relativize(sourceFile.getParent()
                                .toFile()
                                .toURI())
                        .getPath());
    }

}
