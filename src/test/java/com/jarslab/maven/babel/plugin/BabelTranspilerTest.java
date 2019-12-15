package com.jarslab.maven.babel.plugin;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.charset.Charset;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BabelTranspilerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Log log = new SystemStreamLog();

    private BabelTranspiler.BabelTranspilerBuilder babelTranspilerBuilder = BabelTranspiler.builder()
            .verbose(true)
            .log(log)
            .babelSource(TestUtils.getBabelPath().toFile())
            .charset(Charset.forName("UTF-8"));


    @Test
    public void shouldTranspileEs6File() {
        // Given
        TranspileContext context = TranspileContext.builder()
                .source(TestUtils.getBasePath().resolve(Paths.get("src", "a", "test-es6.js")))
                .build();

        BabelTranspiler babelTranspiler = babelTranspilerBuilder
                .presets("'es2015'")
                .build();

        // When
        context = babelTranspiler.execute(context);

        // Then
        assertThat(context.getResult(), is(TestUtils.getResourceAsString("/trans/a/trans-test-es6.js")));
    }

    @Test
    public void shouldTranspileReactFile() {
        // Given
        TranspileContext context = TranspileContext.builder()
                .source(TestUtils.getBasePath().resolve(Paths.get("src", "a", "test-react.js")))
                .build();

        BabelTranspiler babelTranspiler = babelTranspilerBuilder
                .presets("'react'")
                .build();

        context = babelTranspiler.execute(context);

        // Then
        assertThat(context.getResult(), is(TestUtils.getResourceAsString("/trans/a/trans-test-react.js")));
    }

}