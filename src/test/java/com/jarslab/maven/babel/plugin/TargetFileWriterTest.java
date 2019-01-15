package com.jarslab.maven.babel.plugin;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class TargetFileWriterTest
{
    private static final String TMP_DIRECTORY = System.getProperty("java.io.tmpdir");
    private static final String TEST_INPUT = "test";
    private static final String PREFIX = "t.";

    @Test
    public void shouldWriteFileWithoutPrefix() throws IOException
    {
        //given
        final TargetFileWriter targetFileWriter = new TargetFileWriter(
                TestUtils.getBasePath(), TMP_DIRECTORY, "");
        //when
        targetFileWriter.writeTargetFile(
                Paths.get(TestUtils.getBasePath(), "/src/test.js"),
                TEST_INPUT.getBytes());
        //then
        final byte[] bytes = Files.readAllBytes(Paths.get(TMP_DIRECTORY, "/src/test.js"));
        assertThat(bytes).isEqualTo(TEST_INPUT.getBytes());
    }

    @Test
    public void shouldWriteFileWithPrefix() throws IOException
    {
        //given
        final TargetFileWriter targetFileWriter = new TargetFileWriter(
                TestUtils.getBasePath(), TMP_DIRECTORY, PREFIX);
        //when
        targetFileWriter.writeTargetFile(
                Paths.get(TestUtils.getBasePath(), "/src/test.js"),
                TEST_INPUT.getBytes());
        //then
        final byte[] bytes = Files.readAllBytes(Paths.get(TMP_DIRECTORY, "/src/t.test.js"));
        assertThat(bytes).isEqualTo(TEST_INPUT.getBytes());
    }
}