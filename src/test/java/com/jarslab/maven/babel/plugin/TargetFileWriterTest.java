package com.jarslab.maven.babel.plugin;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
        TargetFileWriter targetFileWriter = new TargetFileWriter(Charset.defaultCharset(), log);
        TranspileContext transpileContext = TranspileContext.builder()
                .target(TMP_DIRECTORY.resolve(Paths.get("src", "test.js")))
                .result(TEST_INPUT)
                .build();

        // When
        targetFileWriter.writeTargetFile(transpileContext);

        // Then
        byte[] bytes = Files.readAllBytes(TMP_DIRECTORY.resolve(Paths.get("src", "test.js")));
        assertThat(bytes).isEqualTo(TEST_INPUT.getBytes());
    }

}