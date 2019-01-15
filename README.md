# Babel Maven Plugin
Plugin lets you to execute Babel transcription for given JavaScript files. 
It requires no npm or node.js, it is plain Java project which works perfectly combined with WebJars.

## Settings, ie buttons and knobs
* **`verbose`** - no surprises, the execution becomes a bit more talkative (default: _false_),
* **`babelSrc`** - readable path to standalone(!) Babel sources. It can be provided from WebJars dependency, minified 
or development version,
* **`sourceDir`** - base path for JavaScript files you are going to translate,
* **`targetDir`** - result path, note that all sub-directories from `sourceDir` will be preserved,
* **`jsFiles`** - list of JavaScript files (static)  from `sourceDir` to translate,
* **`jsIncludes`** - list of JavaScript files (with simple masks `*`/`?`),
* **`jsExcludes`** - list of exceptions for `jsIncludes`,
* **`presets`** - presets for Babel execution (default: _es2015_),

