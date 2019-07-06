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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * This test is broken by design.
 * To run it properly it requires jvm option `-Dfile.encoding=windows-1252`.
 * Due to runtime caches System::setProperty will not work as expected.
 */
@RunWith(MockitoJUnitRunner.class)
public class BabelTranspilerCp1252Test
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private TargetFileWriter targetFileWriter;
    @Mock
    private Log log;

    @BeforeClass
    public static void setUpClass()
    {
        System.setProperty("file.encoding", "Cp1252");
    }

    @Test
    public void shouldTranspileCp1252Encoding() throws IOException
    {
        //given
        final File sourceFile = File.createTempFile("babel-test", "");
        final Path sourceFilePath = sourceFile.toPath();
        sourceFile.deleteOnExit();
        Files.write(
                sourceFilePath,
                Files.readAllLines((Paths.get(TestUtils.getBasePath(), "/src/a/test-react.js"))),
                Charset.forName("UTF-8"));
        final BabelTranspiler babelTranspiler = new BabelTranspiler(
                false, log, targetFileWriter,
                Paths.get(TestUtils.getBabelPath()).toFile(),
                sourceFilePath,
                "'react'",
                Charset.forName("UTF-8"));
        //when
        babelTranspiler.execute();
        //then
        verify(targetFileWriter, times(1))
                .writeTargetFile(
                        eq(sourceFilePath),
                        argThat(arg -> arg.contains("createElement")));
    }
}