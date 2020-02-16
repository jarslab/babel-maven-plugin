package com.jarslab.maven.babel.plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class TestUtils
{
    public static Path getBasePath()
    {
        try {
            return Paths.get(TestUtils.class.getResource("/").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getBabelPath()
    {
        try {
            return Paths.get(TestUtils.class.getResource("/babel-7.8.4.min.js").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getResourceAsString(String resource)
    {
        return getResourceAsString(TestUtils.class, resource);
    }

    private static String getResourceAsString(Class<TestUtils> aClass, String resource)
    {
        return new BufferedReader(new InputStreamReader(
                aClass.getResourceAsStream(resource)))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
