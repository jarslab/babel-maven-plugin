![Travis CI](https://api.travis-ci.org/jarslab/babel-maven-plugin.svg) ![Maven Central](https://img.shields.io/maven-central/v/com.jarslab.maven/babel-maven-plugin.svg)

# Babel Maven Plugin
Plugin lets you to execute Babel transcription for given JavaScript files. 
It requires no npm or node.js, it is plain Java project which works perfectly combined with WebJars.

## Maven coords
```xml
<dependency>
  <groupId>com.jarslab.maven</groupId>
  <artifactId>babel-maven-plugin</artifactId>
  <version>1.1</version>
</dependency>
```

## Settings, ie buttons and knobs
* **`verbose`** - no surprises, the execution becomes a bit more talkative (default: _false_),
* **`babelSrc`** - readable path to standalone(!) Babel sources. It can be provided from WebJars dependency, minified 
or development version,
* **`sourceDir`** - base path for JavaScript files you are going to translate,
* **`targetDir`** - result path, note that all sub-directories from `sourceDir` will be preserved,
* **`jsFiles`** - list of JavaScript files (static)  from `sourceDir` to translate,
* **`jsIncludes`** - list of JavaScript files (with simple masks `*`/`?`),
* **`jsExcludes`** - list of exceptions for `jsIncludes`,
* **`prefix`** - optional prefix applied for every translated file,
* **`presets`** - presets for Babel execution (default: _es2015_),

## Example
```xml
<plugin>
    <groupId>com.jarslab.maven</groupId>
    <artifactId>babel-maven-plugin</artifactId>
    <version>1.0</version>
    <executions>
        <execution>
            <id>js-transpile</id>
            <phase>process-resources</phase>
            <goals>
                <goal>babel</goal>
            </goals>
            <configuration>
                <verbose>true</verbose>
                <babelSrc>${project.basedir}/target/classes/assets/jslib/babel.min.js</babelSrc>
                <sourceDir>${project.basedir}/target/classes/assets/</sourceDir>
                <targetDir>${project.basedir}/target/classes/assets/</targetDir>
                <jsSourceIncludes>
                    <jsSourceInclude>src/*.js</jsSourceInclude>
                </jsSourceIncludes>
                <prefix>trans-</prefix>
                <presets>react,es2015</presets>
            </configuration>
        </execution>
    </executions>
</plugin>
```
