package com.jarslab.maven.babel.plugin;

import com.jarslab.maven.babel.plugin.transpiler.ImmutableTranspilation;
import com.jarslab.maven.babel.plugin.transpiler.ImmutableTranspilationContext;
import com.jarslab.maven.babel.plugin.transpiler.Transpilation;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TargetFileWriterTest {

    private static final Path TMP_DIRECTORY = Paths.get(System.getProperty("java.io.tmpdir"));
    private static final String TEST_INPUT = "test";

    @Mock
    private Log log;

    @Test
    public void shouldWriteFile() throws Exception {
        // Given
        Transpilation transpileContext = ImmutableTranspilation.builder()
                .source(Paths.get("foo"))
                .target(TMP_DIRECTORY.resolve(Paths.get("src", "test.js")))
                .result(TEST_INPUT)
                .context(ImmutableTranspilationContext.builder()
                        .babelSource(new File("/"))
                        .presets("'es2015'")
                        .charset(Charset.forName("UTF-8"))
                        .log(log).build())
                .build();

        // When
        TargetFileWriter.writeTargetFile(transpileContext);

        // Then
        byte[] bytes = Files.readAllBytes(TMP_DIRECTORY.resolve(Paths.get("src", "test.js")));
        assertThat(bytes).isEqualTo(TEST_INPUT.getBytes());
    }

}