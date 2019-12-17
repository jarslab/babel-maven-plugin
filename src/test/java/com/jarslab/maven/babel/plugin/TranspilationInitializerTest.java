package com.jarslab.maven.babel.plugin;

import com.jarslab.maven.babel.plugin.transpiler.Transpilation;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TranspilationInitializerTest {

    private BabelMojo.BabelMojoBuilder babelMojoBuilder = BabelMojo.builder()
            .encoding("UTF-8")
            .babelSrc(TestUtils.getBabelPath().toFile())
            .sourceDir(TestUtils.getBasePath().toFile())
            .targetDir(Paths.get("foo").toFile())
            .presets("es2015");

    @Test
    public void shouldNotFailForEmptyParameters() {
        // Given
        BabelMojo babelMojo = babelMojoBuilder.build();

        // When
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();

        // Then
        assertThat(transpilations).isEmpty();
    }

    @Test
    public void shouldGetFilesFromStaticList() {
        // Given
        BabelMojo babelMojo = babelMojoBuilder
                .jsSourceFile("/src/test.js")
                .build();

        // When
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();

        // Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test.js");
    }


    @Test
    public void shouldGetFilesFromIncludesList() {
        // Given
        BabelMojo babelMojo = babelMojoBuilder
                .jsSourceInclude("/src/a/test-*.js")
                .build();

        // When
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();

        //Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test-es6.js", "test-react.js");
    }

    @Test
    public void shouldGetFilesFromIncludesListUsingFileSeparator() {
        // Given
        BabelMojo babelMojo = babelMojoBuilder
                .jsSourceInclude(File.separator + "src" + File.separator + "a" + File.separator + "test-*.js")
                .build();

        // When
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();

        //Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test-es6.js", "test-react.js");
    }

    @Test
    public void shouldGetFilesFromIncludesListApplyingExclude() {
        //Given
        BabelMojo babelMojo = babelMojoBuilder
                .jsSourceInclude("/src/a/test-*.js")
                .jsSourceExclude("/src/a/*react*")
                .build();

        // When
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();

        //Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test-es6.js");
    }

    @Test
    public void shouldGetFilesFromAllParameters() {
        // Given
        BabelMojo babelMojo = babelMojoBuilder
                .jsSourceFile("/src/test.js")
                .jsSourceInclude("/src/a/test-*.js")
                .jsSourceExclude("/src/a/*es6.js")
                .build();

        // When
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();

        // Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test.js", "test-react.js");
    }

    @Test
    public void shouldMapRelatively() {
        // Given
        Path targetDirectory = Paths.get("some", "target", "path");
        BabelMojo mojo = babelMojoBuilder
                .targetDir(targetDirectory.toFile())
                .jsSourceFile("/src/test.js")
                .build();

        TranspilationInitializer transpilationInitializer = new TranspilationInitializer(mojo);

        // When
        Set<Transpilation> transpilations = transpilationInitializer.getTranspilations();

        // Then
        Path targetFile = transpilations.iterator().next().getTarget();
        assertThat(targetDirectory.relativize(targetFile)).isEqualTo(Paths.get("src", "test.js"));
    }

    @Test
    public void shouldAddPrefix() {
        // Given
        BabelMojo babelMojo = babelMojoBuilder
                .jsSourceFile("/src/test.js")
                .prefix("some-prefix-")
                .build();

        // When
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();

        // Then
        String fileName = transpilations.iterator().next().getTarget().getFileName().toString();
        assertThat(fileName).isEqualTo("some-prefix-test.js");
    }

    private Stream<String> getSourceFilesNames(Set<Transpilation> transpilations) {
        return transpilations.parallelStream()
                .map(Transpilation::getSource)
                .map(Path::getFileName)
                .map(Path::toString);
    }

}