package com.jarslab.maven.babel.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class BabelMojoTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldFailForNotExistedBabelPath() throws MojoFailureException, MojoExecutionException
    {
        //given
        final BabelMojo babelMojo = getBabelMojo();
        babelMojo.setBabelSrc(Paths.get(TestUtils.getBasePath(), "bagel.min.js").toString());
        //expect
        expectedException.expect(MojoFailureException.class);
        //when
        babelMojo.execute();
    }

    @Test
    public void shouldFailForNoPresets() throws MojoFailureException, MojoExecutionException
    {
        //given
        final BabelMojo babelMojo = getBabelMojo();
        babelMojo.setPresets("");
        //expect
        expectedException.expect(MojoFailureException.class);
        //when
        babelMojo.execute();
    }

    @Test
    public void shouldDoNothingForMissingSourceFiles() throws MojoFailureException, MojoExecutionException
    {
        //given
        final BabelMojo babelMojo = getBabelMojo();
        babelMojo.setJsSourceFiles(Collections.emptyList());
        babelMojo.setJsSourceIncludes(Collections.emptyList());
        //when
        babelMojo.execute();
        //then
        //pass
    }

    @Test
    public void shouldRunCompleteExecution() throws MojoFailureException, MojoExecutionException
    {
        //given
        final BabelMojo babelMojo = getBabelMojo();
        //when
        babelMojo.execute();
        //then
        assertThat(Paths.get(System.getProperty("java.io.tmpdir"), "/src/a/")).exists();
    }

    private BabelMojo getBabelMojo()
    {
        final BabelMojo babelMojo = new BabelMojo();
        babelMojo.setVerbose(false);
        babelMojo.setBabelSrc(TestUtils.getBabelPath());
        babelMojo.setSourceDir(TestUtils.getBasePath());
        babelMojo.setTargetDir(System.getProperty("java.io.tmpdir"));
        babelMojo.setJsSourceFiles(Arrays.asList("/src/test.js"));
        babelMojo.setJsSourceIncludes(Arrays.asList("/src/a/*.js"));
        babelMojo.setJsSourceExcludes(Arrays.asList("/src/a/*react.js"));
        babelMojo.setPrefix("t.");
        babelMojo.setPresets("es2015,react");
        return babelMojo;
    }
}