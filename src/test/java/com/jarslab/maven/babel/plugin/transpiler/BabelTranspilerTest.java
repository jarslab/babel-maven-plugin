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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BabelTranspilerTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Log log = new SystemStreamLog();

    private ImmutableTranspilationContext.Builder contextBuilder = ImmutableTranspilationContext.builder()
            .isVerbose(true)
            .log(log)
            .babelSource(TestUtils.getBabelPath().toFile())
            .charset(Charset.forName("UTF-8"));


    @Test
    public void shouldTranspileEs6File()
    {
        //given
        Transpilation transpilation = ImmutableTranspilation.builder()
                .source(TestUtils.getBasePath().resolve(Paths.get("src", "a", "test-es6.js")))
                .target(Paths.get("foo"))
                .context(contextBuilder.presets("'es2015'").build())
                .build();
        //when
        transpilation = new BabelTranspiler().execute(transpilation);
        //then
        assertThat(transpilation.getResult()).get().isEqualTo(TestUtils.getResourceAsString("/trans/a/trans-test-es6.js"));
    }

    @Test
    public void shouldTranspileReactFile()
    {
        //given
        Transpilation transpilation = ImmutableTranspilation.builder()
                .source(TestUtils.getBasePath().resolve(Paths.get("src", "a", "test-react.js")))
                .target(Paths.get("foo"))
                .context(contextBuilder.presets("'react'").build())
                .build();
        //when
        transpilation = new BabelTranspiler().execute(transpilation);
        //then
        assertThat(transpilation.getResult()).get().isEqualTo(TestUtils.getResourceAsString("/trans/a/trans-test-react.js"));
    }

    @Test
    public void shouldTranspileAsyncFile()
    {
        //given
        Transpilation transpilation = ImmutableTranspilation.builder()
                .source(TestUtils.getBasePath().resolve(Paths.get("src", "a", "test-async.js")))
                .target(Paths.get("foo"))
                .context(contextBuilder.presets("'es2017'").build())
                .build();
        //when
        transpilation = new BabelTranspiler().execute(transpilation);
        //then
        assertThat(transpilation.getResult()).get().isEqualTo(TestUtils.getResourceAsString("/trans/a/trans-test-async.js"));
    }

    @Test
    public void shouldTranspileAsyncFileWithPlugins()
    {
        //given
        Transpilation transpilation = ImmutableTranspilation.builder()
                .source(TestUtils.getBasePath().resolve(Paths.get("src", "a", "test-async.js")))
                .target(Paths.get("foo"))
                .context(contextBuilder
                        .presets("'es2017'")
                        .plugins("['transform-runtime', {'regenerator': true}]")
                        .build())
                .build();
        //when
        transpilation = new BabelTranspiler().execute(transpilation);
        //then
        assertThat(transpilation.getResult()).get().isEqualTo(TestUtils.getResourceAsString("/trans/a/trans-test-plugin-async.js"));
    }
}