package com.jarslab.maven.babel.plugin;

import com.jarslab.maven.babel.plugin.transpiler.Transpilation;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TranspilationInitializerTest
{
    private BabelMojo babelMojo;

    @Before
    public void setUp()
    {
        babelMojo = new BabelMojo();
        babelMojo.setEncoding("UTF-8");
        babelMojo.setBabelSrc(TestUtils.getBabelPath().toFile());
        babelMojo.setSourceDir(TestUtils.getBasePath().toFile());
        babelMojo.setTargetDir(Paths.get("foo").toFile());
        babelMojo.setPresets("es2015");
    }

    @Test
    public void shouldNotFailForEmptyParameters()
    {
        //given
        //babelMojo
        //when
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();
        //then
        assertThat(transpilations).isEmpty();
    }

    @Test
    public void shouldGetFilesFromStaticList()
    {
        //given
        babelMojo.setJsSourceFile("/src/test.js");
        //when
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();
        //then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test.js");
    }


    @Test
    public void shouldGetFilesFromIncludesList()
    {
        //given
        babelMojo.setJsSourceInclude("/src/a/test-*.js");
        //when
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();
        //Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test-es6.js", "test-react.js", "test-async.js");
    }

    @Test
    public void shouldGetFilesFromIncludesListUsingFileSeparator()
    {
        //given
        babelMojo.setJsSourceInclude(File.separator + "src" + File.separator + "a" + File.separator + "test-*.js");
        //when
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();
        //Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test-es6.js", "test-react.js", "test-async.js");
    }

    @Test
    public void shouldGetFilesFromIncludesListApplyingExclude()
    {
        //Given
        babelMojo.setJsSourceInclude("/src/a/test-*.js");
        babelMojo.setJsSourceExclude("/src/a/*react*");
        //when
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();
        //Then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test-es6.js", "test-async.js");
    }

    @Test
    public void shouldGetFilesFromAllParameters()
    {
        //given
        babelMojo.setJsSourceFile("/src/test.js");
        babelMojo.setJsSourceInclude("/src/a/test-*.js");
        babelMojo.setJsSourceExclude("/src/a/*es6.js");
        //when
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();
        //then
        assertThat(getSourceFilesNames(transpilations)).containsOnly("test.js", "test-react.js", "test-async.js");
    }

    @Test
    public void shouldMapRelatively()
    {
        //given
        Path targetDirectory = Paths.get("some", "target", "path");
        babelMojo.setTargetDir(targetDirectory.toFile());
        babelMojo.setJsSourceFile("/src/test.js");
        TranspilationInitializer transpilationInitializer = new TranspilationInitializer(babelMojo);
        //when
        Set<Transpilation> transpilations = transpilationInitializer.getTranspilations();
        //then
        Path targetFile = transpilations.iterator().next().getTarget();
        assertThat(targetDirectory.relativize(targetFile)).isEqualTo(Paths.get("src", "test.js"));
    }

    @Test
    public void shouldAddPrefix()
    {
        //given
        babelMojo.setJsSourceFile("/src/test.js");
        babelMojo.setPrefix("some-prefix-");
        //when
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();
        //then
        String fileName = transpilations.iterator().next().getTarget().getFileName().toString();
        assertThat(fileName).isEqualTo("some-prefix-test.js");
    }

    @Test
    public void shouldFormatPresets()
    {
        //given
        babelMojo.setPresets("test,      test,test");
        babelMojo.setJsSourceFile("/src/test.js");
        //when
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();
        //then
        final String presets = transpilations.iterator().next().getContext().getPresets();
        assertThat(presets).isEqualTo("'test','test','test'");
    }

    @Test
    public void shouldNotFormatPresets()
    {
        //given
        babelMojo.setPresets("test without formatting");
        babelMojo.setFormatPresets(false);
        babelMojo.setJsSourceFile("/src/test.js");
        //when
        Set<Transpilation> transpilations = new TranspilationInitializer(babelMojo).getTranspilations();
        //then
        final String presets = transpilations.iterator().next().getContext().getPresets();
        assertThat(presets).isEqualTo("test without formatting");
    }

    private Stream<String> getSourceFilesNames(Set<Transpilation> transpilations)
    {
        return transpilations.parallelStream()
                .map(Transpilation::getSource)
                .map(Path::getFileName)
                .map(Path::toString);
    }

}