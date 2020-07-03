![Travis CI](https://api.travis-ci.org/jarslab/babel-maven-plugin.svg) ![Maven Central](https://img.shields.io/maven-central/v/com.jarslab.maven/babel-maven-plugin.svg)

# Babel Maven Plugin
Plugin lets you to execute Babel transcription for given JavaScript files. 
It requires no npm or node.js, it is plain Java project (based on embedded GraalVM from version 1.4) which works perfectly combined with WebJars.

## Maven coords
```xml
<dependency>
  <groupId>com.jarslab.maven</groupId>
  <artifactId>babel-maven-plugin</artifactId>
  <version>1.6</version>
</dependency>
```

## Settings, ie buttons and knobs
* **`verbose`** - no surprises, the execution becomes a bit more talkative (default: _false_),
* **`threads`** - number of threads to use when transpiling (default: _1_, will be capped on the amount of processors available)
* **`encoding`** - will apply chosen encoding during files operations (read/write) (default: `Charset.defaultCharset()`),
* **`babelSrc`** - readable path to standalone(!) Babel sources. It can be provided from WebJars dependency, minified 
or development version,
* **`sourceDir`** - base path for JavaScript files you are going to translate,
* **`targetDir`** - result path, note that all sub-directories from `sourceDir` will be preserved,
* **`jsFiles`** - list of JavaScript files (static)  from `sourceDir` to translate,
* **`jsIncludes`** - list of JavaScript files (with simple masks `*`/`?`),
* **`jsExcludes`** - list of exceptions for `jsIncludes`,
* **`prefix`** - optional prefix applied for every translated file,
* **`targetFileExtension`** - if specified, the extension of every translated file will be set to this string (e.g., `js`).
* **`formatPresets`** - enable/disable presets formatting (default: _true_). Once disabled `presets` are required to be well formatted,  
* **`presets`** - presets for Babel execution (default: _es2015_),
* **`plugins`** - plugins for Babel execution (default: _""_ (empty)) _NOTE: any custom plugins are required to be available in provided `babelSrc`_

## Example
```xml
<plugin>
    <groupId>com.jarslab.maven</groupId>
    <artifactId>babel-maven-plugin</artifactId>
    <version>1.6</version>
    <executions>
        <execution>
            <id>js-transpile</id>
            <phase>process-resources</phase>
            <goals>
                <goal>babel</goal>
            </goals>
            <configuration>
                <verbose>true</verbose>
                <threads>4</threads>
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

## Changelog
* **1.6**: Fix Presets handling
* **1.5**: Add `plugins` option for Babel execution
* **1.4**: Switch from deprecated Nashorn engine to GraalVM
