package com.jarslab.maven.babel.plugin;

import org.codehaus.plexus.util.DirectoryScanner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

class SourceFilesExtractor
{
    private final String baseDirectory;
    private final List<String> files;
    private final List<String> includes;
    private final List<String> excludes;

    SourceFilesExtractor(final String baseDirectory,
                         final List<String> files,
                         final List<String> includes,
                         final List<String> excludes)
    {
        this.baseDirectory = requireNonNull(baseDirectory);
        this.files = unmodifiableList(files);
        this.includes = unmodifiableList(includes);
        this.excludes = unmodifiableList(excludes);
    }

    Set<Path> getSourceFiles()
    {
        final Set<Path> sourceFiles = new HashSet<>();
        files.stream()
                .map(file -> Paths.get(baseDirectory, file))
                .filter(p -> p.toFile().exists())
                .forEach(sourceFiles::add);
        if (!includes.isEmpty()) {
            Stream.of(getIncludesDirectoryScanner().getIncludedFiles())
                    .map(file -> Paths.get(baseDirectory, file))
                    .forEach(sourceFiles::add);
        }
        return sourceFiles;
    }

    private DirectoryScanner getIncludesDirectoryScanner()
    {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(includes.toArray(new String[includes.size()]));
        scanner.setExcludes(excludes.toArray(new String[excludes.size()]));
        scanner.addDefaultExcludes();
        scanner.setBasedir(baseDirectory);
        scanner.scan();
        return scanner;
    }
}
