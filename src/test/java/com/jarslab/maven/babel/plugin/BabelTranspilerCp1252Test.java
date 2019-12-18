package com.jarslab.maven.babel.plugin;

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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

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

        BabelTranspiler babelTranspiler = BabelTranspiler.builder()
                .log(log)
                .babelSource(TestUtils.getBabelPath().toFile())
                .presets("'react'")
                .charset(Charset.forName("UTF-8"))
                .build();

        TranspileContext context = TranspileContext.builder()
                .source(sourceFilePath)
                .build();

        // When
        context = babelTranspiler.execute(context);

        // Then
        assertThat(context.getResult(), containsString("createElement"));
    }

}