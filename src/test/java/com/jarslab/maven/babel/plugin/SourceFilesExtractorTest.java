package com.jarslab.maven.babel.plugin;

import org.junit.Test;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SourceFilesExtractorTest
{
    @Test
    public void shouldNotFailForEmptyParameters()
    {
        //given
        final SourceFilesExtractor sourceFilesExtractor = new SourceFilesExtractor(
                "",
                Arrays.asList(),
                Arrays.asList(),
                Arrays.asList());
        //when
        final Set<Path> sourceFiles = sourceFilesExtractor.getSourceFiles();
        //then
        assertThat(sourceFiles).isEmpty();
    }

    @Test
    public void shouldGetFilesFromStaticList()
    {
        //given
        final SourceFilesExtractor sourceFilesExtractor = new SourceFilesExtractor(
                TestUtils.getBasePath(),
                Arrays.asList("/src/test.js"),
                Arrays.asList(),
                Arrays.asList());
        //when
        final Set<Path> sourceFiles = sourceFilesExtractor.getSourceFiles();
        //then
        assertThat(getFilesNames(sourceFiles)).containsOnly("test.js");
    }

    @Test
    public void shouldGetFilesFromIncludesList()
    {
        //given
        final SourceFilesExtractor sourceFilesExtractor = new SourceFilesExtractor(
                TestUtils.getBasePath(),
                Arrays.asList(),
                Arrays.asList("/src/a/test-*.js"),
                Arrays.asList());
        //when
        final Set<Path> sourceFiles = sourceFilesExtractor.getSourceFiles();
        //then
        assertThat(getFilesNames(sourceFiles)).containsOnly("test-es6.js", "test-react.js");
    }

    @Test
    public void shouldGetFilesFromIncludesListApplyingExclude()
    {
        //given
        final SourceFilesExtractor sourceFilesExtractor = new SourceFilesExtractor(
                TestUtils.getBasePath(),
                Arrays.asList(),
                Arrays.asList("/src/a/test-*.js"),
                Arrays.asList("/src/a/*react*"));
        //when
        final Set<Path> sourceFiles = sourceFilesExtractor.getSourceFiles();
        //then
        assertThat(getFilesNames(sourceFiles)).containsOnly("test-es6.js");
    }

    @Test
    public void shouldGetFilesFromAllParameters()
    {
        //given
        final SourceFilesExtractor sourceFilesExtractor = new SourceFilesExtractor(
                TestUtils.getBasePath(),
                Arrays.asList("/src/test.js"),
                Arrays.asList("/src/a/test-*.js"),
                Arrays.asList("/src/a/*es6.js"));
        //when
        final Set<Path> sourceFiles = sourceFilesExtractor.getSourceFiles();
        //then
        assertThat(getFilesNames(sourceFiles)).containsOnly("test.js", "test-react.js");
    }

    private Stream<String> getFilesNames(final Set<Path> sourceFiles)
    {
        return sourceFiles.stream()
                .map(Path::getFileName)
                .map(Path::toString);
    }
}