![Travis CI](https://api.travis-ci.org/jarslab/babel-maven-plugin.svg) ![Maven Central](https://img.shields.io/maven-central/v/com.jarslab.maven/babel-maven-plugin.svg)

# Babel Maven Plugin
Plugin lets you to execute Babel transcription for given JavaScript files. 
It requires no npm or node.js, it is plain Java project which works perfectly combined with WebJars.

## Maven coords
```xml
<dependency>
  <groupId>com.jarslab.maven</groupId>
  <artifactId>babel-maven-plugin</artifactId>
  <version>1.2</version>
</dependency>
```

## Settings, ie buttons and knobs
* **`verbose`** - no surprises, the execution becomes a bit more talkative (default: _false_),
* **`strategy`** - Can be either `PARALLEL`, `SEQUENTIAL`, or `MIXED`, determining the parallelism whilst transpiling (default 'SEQUENTIAL'. For backwards compatibility, if ~~**`parallel`**~~ is set to true, **`strategy`** defaults to `PARALLEL`):
   * `SEQUENTIAL` - Initializes a single `ScriptEngine` and sequentially transpiles each source file, using that same engine. Because initializing the `ScriptEngine` is quite an expensive operation compared to transpiling the source files, this option is probably most suitable for transpiling lots of small files.
   * `PARALLEL` - transpiles all sources files using a parallel stream. This goes throught he source files, initializes a ScriptEngine for each of them and using that Enginge to transpile the file. This is a naive approach to parallelism, and as such probably not suitable for most projects.
   * `MIXED` - an alternative to the `PARALLEL` strategy, in which a pool of workes is created, which run in parallel, each creating a `ScriptEngine` and using that to sequentially proces as many source files as it can put it's hands on. This strategy is most suitable for transpiling lots of large files, as the cost of initializing those engines weighs up to the benefit of transpiling in parallel.
* ~~**`parallel`** - if true will run files transpilation in parallel (on ForkJoin pool) (default: _true_),~~ **deprecated** in favor of **`transpileStrategy`**. When 
* **`encoding`** - will apply chosen encoding during files operations (read/write) (default: `Charset.defaultCharset()`),
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
    <version>1.2</version>
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
