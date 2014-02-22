## v0.3.0-beta
- Autoescaping, more escaping strategies, autoescape tag, and raw filter.
- Extensions can now provide node visitors to traverse the AST.
- Macros can have default argument values.
- Implemented dynamic inheritance.
- Renamed 'message' function to 'i18n'
- Fixed issue where compilation failed in JBoss.
- Code cleanup and misc small bugs

## v0.2.0-beta (2014-02-08)
- Implemented named arguments.
- Added dependency on google guava for template cache.
- Split the default loader class into multiple discrete loaders.
- Added the `title` filter.
- Fixed issue where compilation mutex might not have been released.
- Fixed parsing issues if variable names were prefixed with operator names.
- Fixed issue where included templates didn't have access to context.
- Fixed issue where `if` tag could not be used directly on a boolean variable.
- Removed the `format` filter.
- Fixed misc other smaller bugs.

## v0.1.5-beta (2014-01-27)
- Fixed major bug from v0.1.4 that prevented macros from being invoked more than once.

## v0.1.4-beta (2014-01-27)
- The i18n extension is now enabled by default.
- Improved exception handling (storing cause where applicable).
- PebbleEngine now returns a PebbleTemplate interface with a small subset of original methods.
- Refactored function/filter/test interfaces into functional interfaces (preparation for Java 8).

## v0.1.3-beta (2014-01-25)
- More unit tests and minor bug fixes.
- Fixed issue where child templates were being inappropriately cached.
- All core filters now perform null checking.
- Performance optimization with variable attributes.
- Renamed the number_format filter to numberformat 

## v0.1.2-beta (2014-01-19)
- Fixed issue where parent block didn’t have access to context.
- Macros no longer have access to context (only local vars).
- Fixed issue where macro output coudn’t be filtered/tested.
- Refactored how blocks and macros are implemented .
- Renamed number filter to number_format.
- Added a cache interface for user’s to provide their own cache. Also removed the “cacheTemplates” setting.
- Default cache is now thread safe.
- Templates can now be evaluated concurrently.
- Users can now safely attempt a concurrent compilation.
- Fixed issue where provided writer was being closed by pebble engine.
- Fixed memory leak in file manager.
- Removed json filter.
- Removed some third party dependencies.
- Added parallel tag.
- More unit tests and misc code cleanup.

## v0.1.1-beta (2014-01-02)
- Fixed issue where templates of same name but different path were overriding each other in main template cache.
- Made sure byte code stored in memory in InMemoryJavaFileManager is cleared when no longer required.
- Removed caching of Reader objects from PebbleDefaultLoader which was causing more harm than good. This can be added back later if it is deemed necessary.
- Completely changed how operators are compiled into Java due to a bunch of bugs regarding operand types.
- Changed the behaviour of the == operator and added the equals operator as an alias.
- Extensions can now provide custom functions.
- Added source, min, and max functions.
- The setting, cacheTemplates, now defaults to true.
- Renamed the main entry points into the main Engine from “loadTemplate/render” to “compile/evaluate”.
- Added i18n extension (disabled by default) and a default locale setting on the main pebble engine. The extension adds one new function: message()
- Small performance improvements when looking up variable attributes.

## v0.1.0-beta (2013-12-27)
- Refined PebbleEngine’s available public methods.
- Added “strictVariables” setting to PebbleEngine.
- Cleaned up how pebble-spring is to be configured.
- More bug fixes and unit tests.

## v0.0.3-alpha (2013-11-17)
- Configuration changes in order to have the project hosted in the Maven Central Repository.

## v0.0.2-alpha (2013-11-16)
- Dedicated website with documentation.
- Code refactoring, more unit tests, bug fixes.
- Conditional (ternary) operator.
- Escape filter.
- Macro overloading.

## v0.0.1-alpha (2013-09-30)
This is the first functioning version of Pebble. The following has been implemented:
- tags: block, extends, for, if, import, include, macro, set
- filters: abbreviate, capitalize, date, default, format, json, lower, number, trim, upper, urlencode
- functions: block, parent
- tests: empty, even, null, odd, iterable, equalTo
- operators: in, is, is not, +, -, /, *, %, and, or, (), ==, !=, <, >, <=, >=, |, .
- unit tests
