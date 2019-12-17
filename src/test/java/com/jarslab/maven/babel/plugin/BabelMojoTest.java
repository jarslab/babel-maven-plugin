package com.jarslab.maven.babel.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Paths;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class BabelMojoTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldFailForNotExistedBabelPath() throws MojoFailureException, MojoExecutionException {
        //given
        final BabelMojo babelMojo = getBabelMojo();
        babelMojo.setBabelSrc(TestUtils.getBasePath().resolve("bagel.min.js").toFile());
        //expect
        expectedException.expect(MojoFailureException.class);
        //when
        babelMojo.execute();
    }

    @Test
    public void shouldFailForNoPresets() throws MojoFailureException, MojoExecutionException {
        //given
        final BabelMojo babelMojo = getBabelMojo();
        babelMojo.setPresets("");
        //expect
        expectedException.expect(MojoFailureException.class);
        //when
        babelMojo.execute();
    }

    @Test
    public void shouldDoNothingForMissingSourceFiles() throws MojoFailureException, MojoExecutionException {
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
    public void shouldRunCompleteExecution() throws MojoFailureException, MojoExecutionException {
        //given
        final BabelMojo babelMojo = getBabelMojo();
        //when
        babelMojo.execute();
        //then
        assertThat(Paths.get(System.getProperty("java.io.tmpdir")).resolve(Paths.get("src", "a"))).exists();
    }

    private BabelMojo getBabelMojo() {
        return BabelMojo.builder()
                .verbose(true)
                .babelSrc(TestUtils.getBabelPath().toFile())
                .sourceDir(TestUtils.getBasePath().toFile())
                .targetDir(Paths.get(System.getProperty("java.io.tmpdir")).toFile())
                .jsSourceFile("/src/test.js")
                .jsSourceInclude("/src/a/*.js")
                .jsSourceExclude("/src/a/*react.js")
                .prefix("t.")
                .presets("es2015,react")
                .encoding("UTF-8")
                .build();
    }

}