package com.jarslab.maven.babel.plugin.transpiler;

import com.jarslab.maven.babel.plugin.TestUtils;
import org.apache.maven.plugin.logging.Log;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test is broken by design.
 * To run it properly it requires jvm option `-Dfile.encoding=windows-1252`.
 * Due to runtime caches System::setProperty will not work as expected.
 */
@RunWith(MockitoJUnitRunner.class)
public class BabelTranspilerCp1252Test {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Log log;

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("file.encoding", "Cp1252");
    }

    @Test
    public void shouldTranspileCp1252Encoding() throws Exception {
        // Given
        File sourceFile = File.createTempFile("babel-test", "");
        Path sourceFilePath = sourceFile.toPath();
        sourceFile.deleteOnExit();
        Files.write(
                sourceFilePath,
                Files.readAllLines(TestUtils.getBasePath().resolve("src/a/test-react.js")),
                Charset.forName("UTF-8"));

        Transpilation transpilation = ImmutableTranspilation.builder()
                .source(sourceFilePath)
                .target(Paths.get("foo"))
                .context(ImmutableTranspilationContext.builder()
                        .log(log)
                        .babelSource(TestUtils.getBabelPath().toFile())
                        .presets("'react'")
                        .charset(Charset.forName("UTF-8"))
                        .build())
                .build();

        // When
        transpilation = new BabelTranspiler().execute(transpilation);

        // Then
        assertThat(transpilation.getResult()).isPresent();
        //noinspection OptionalGetWithoutIsPresent
        assertThat(transpilation.getResult().get()).contains("createElement");
    }

}