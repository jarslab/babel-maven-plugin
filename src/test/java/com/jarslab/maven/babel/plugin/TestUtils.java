package com.jarslab.maven.babel.plugin;

class TestUtils
{
    static String getBasePath()
    {
        return getBabelPath().replace("/babel.min.js", "");
    }

    static String getBabelPath()
    {
        return TestUtils.class.getResource("/babel.min.js").getPath();
    }
}
