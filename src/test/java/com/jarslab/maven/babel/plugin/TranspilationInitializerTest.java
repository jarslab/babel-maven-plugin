package com.jarslab.maven.babel.plugin;

import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TranspilationInitializerTest {

    @Test
    public void shouldNotFailForEmptyParameters() {
        // Given
        TranspilationInitializer transpilationInitializer = TranspilationInitializer.builder()
                .sourceDirectory(Paths.get("foo"))
                .targetDirectory(Paths.get("bar"))
                .build();

        // When
        Set<TranspileContext> transpilations = transpilationInitializer.getTranspilations();

        // Then
        assertThat(transpilations).isEmpty();
    }

    @Test
    public void shouldGetFilesFromStaticList() {
        // Given
        TranspilationInitializer transpilationInitializer = TranspilationInitializer.builder()
                .sourceDirectory(TestUtils.getBasePath())
                .targetDirectory(Paths.get("bar"))
                .file("/src/test.js")
                .build();

        // When
        Set<TranspileContext> transpilations = transpilationInitializer.getTranspilations();

        // Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test.js");
    }

    @Test
    public void shouldGetFilesFromIncludesList() {
        // Given
        TranspilationInitializer transpilationInitializer = TranspilationInitializer.builder()
                .sourceDirectory(TestUtils.getBasePath())
                .targetDirectory(Paths.get("bar"))
                .include("/src/a/test-*.js")
                .build();

        // When
        Set<TranspileContext> transpilations = transpilationInitializer.getTranspilations();

        //Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test-es6.js", "test-react.js");
    }

    @Test
    public void shouldGetFilesFromIncludesListUsingFileSeparator() {
        // Given
        TranspilationInitializer transpilationInitializer = TranspilationInitializer.builder()
                .sourceDirectory(TestUtils.getBasePath())
                .targetDirectory(Paths.get("bar"))
                .include(File.separator + "src" + File.separator + "a" + File.separator + "test-*.js")
                .build();

        // When
        Set<TranspileContext> transpilations = transpilationInitializer.getTranspilations();

        //Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test-es6.js", "test-react.js");
    }

    @Test
    public void shouldGetFilesFromIncludesListApplyingExclude() {
        //Given
        TranspilationInitializer transpilationInitializer = TranspilationInitializer.builder()
                .sourceDirectory(TestUtils.getBasePath())
                .targetDirectory(Paths.get("bar"))
                .include("/src/a/test-*.js")
                .exclude("/src/a/*react*")
                .build();

        // When
        Set<TranspileContext> transpilations = transpilationInitializer.getTranspilations();

        //Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test-es6.js");
    }

    @Test
    public void shouldGetFilesFromAllParameters() {
        // Given
        TranspilationInitializer transpilationInitializer = TranspilationInitializer.builder()
                .sourceDirectory(TestUtils.getBasePath())
                .targetDirectory(Paths.get("bar"))
                .file("/src/test.js")
                .include("/src/a/test-*.js")
                .exclude("/src/a/*es6.js")
                .build();

        // When
        Set<TranspileContext> transpilations = transpilationInitializer.getTranspilations();

        // Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test.js", "test-react.js");
    }

    @Test
    public void shouldMapRelatively() {
        // Given
        Path targetDirectory = Paths.get("some", "target", "path");
        TranspilationInitializer transpilationInitializer = TranspilationInitializer.builder()
                .sourceDirectory(TestUtils.getBasePath())
                .targetDirectory(targetDirectory)
                .file("/src/test.js")
                .build();

        // When
        Set<TranspileContext> transpilations = transpilationInitializer.getTranspilations();

        // Then
        Path targetFile = transpilations.iterator().next().getTarget();
        assertThat(targetDirectory.relativize(targetFile)).isEqualTo(Paths.get("src", "test.js"));

    }

    @Test
    public void shouldAddPrefix() {
        // Given
        TranspilationInitializer transpilationInitializer = TranspilationInitializer.builder()
                .sourceDirectory(TestUtils.getBasePath())
                .targetDirectory(Paths.get("foo"))
                .file("/src/test.js")
                .prefix("some-prefix-")
                .build();

        // When
        Set<TranspileContext> transpilations = transpilationInitializer.getTranspilations();

        // Then
        String fileName = transpilations.iterator().next().getTarget().getFileName().toString();
        assertThat(fileName).isEqualTo("some-prefix-test.js");
    }

    private Stream<String> getSourceFilesNames(Set<TranspileContext> transpilations) {
        return transpilations.parallelStream()
                .map(TranspileContext::getSource)
                .map(Path::getFileName)
                .map(Path::toString);
    }

}