package com.jarslab.maven.babel.plugin.transpiler;

import com.jarslab.maven.babel.plugin.TestUtils;
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

    private ImmutableTranspilationContext.Builder contextBuilder = ImmutableTranspilationContext.builder()
            .isVerbose(true)
            .log(log)
            .babelSource(TestUtils.getBabelPath().toFile())
            .charset(Charset.forName("UTF-8"));


    @Test
    public void shouldTranspileEs6File() {
        // Given
        Transpilation transpilation = ImmutableTranspilation.builder()
                .source(TestUtils.getBasePath().resolve(Paths.get("src", "a", "test-es6.js")))
                .target(Paths.get("foo"))
                .context(contextBuilder.presets("'es2015'").build())
                .build();

        // When
        transpilation = new BabelTranspiler().execute(transpilation);

        // Then
        assertThat(transpilation.getResult().isPresent(), is(true));
        assertThat(transpilation.getResult().get(), is(TestUtils.getResourceAsString("/trans/a/trans-test-es6.js")));
    }

    @Test
    public void shouldTranspileReactFile() {
        // Given
        Transpilation transpilation = ImmutableTranspilation.builder()
                .source(TestUtils.getBasePath().resolve(Paths.get("src", "a", "test-react.js")))
                .target(Paths.get("foo"))
                .context(contextBuilder.presets("'react'").build())
                .build();

        transpilation = new BabelTranspiler().execute(transpilation);

        // Then
        assertThat(transpilation.getResult().isPresent(), is(true));
        assertThat(transpilation.getResult().get(), is(TestUtils.getResourceAsString("/trans/a/trans-test-react.js")));
    }

}