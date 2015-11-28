## v2.0.0 (to be announced)
- Support for relative template paths
- Added a "cache" tag to cache portions of a template
- String concatenation with "~" operator
- Length filter
- Support for parallel template parsing
- Additional loop variables: last, first, revindex
- "equals" support for enums
- Added a range function and ability to iterate over a range of characters/numbers
- Added support for BigDecimals
- Expressions are permitted within square bracket notation when accessing maps/arrays/lists
- Better error handling with more informative exceptions
- Ability to add extra variables to the context when "including" another template
- Bug fix: "defined" test did not work as expected in strict mode
- Bug fix: fixed broken "less than equal" comparison 

## v1.6.0 (2015-09-06)
- Support for custom escaping strategies
- Support for calling bean methods with primitive argument types
- Bug fix: Fixed issue with delegating loader where it would only use last loader in list of children loaders.

## v1.5.2 (2015-08-30)
- Added rsort filter
- i18n extension now supports dynamic variables
- Bug fix: Failure to subscript an array of primitive type
- Bug fix: Global variables were not accessible in template
- Bug fix: Removed invocation of Character.isAlphabetic which is not supported on older android APIs

## v1.5.1 (2015-06-29)
- New runtime exception thrown if there's an error invoking a member found via reflection
- New constructor in ClasspathLoader that accepts a custom classloader
- Bug fix: Fixed path separator used in ClasspathLoader to work on Windows
- Bug fix: Fixed path separator used in ServletLoader to work on Windows 

## v1.5.0 (2015-06-07)
- Added array and map syntax 

## v1.4.5 (2015-04-17)
- Fixed stack overflow error when using multiple levels of the parent() function
- Fixed platform dependent issues with junit tests
- Bug fix regarding the use of different data types passed to same template which broke an internal cache in the GetAttributeExpression

## v1.4.4 (2015-03-29)
- Bug fix regarding NPE with internal cache
- Bug fix when using suffix with the file loader

## v1.4.3 (2015-03-01)
- Bug fix for issue regarding multiple for loops only rendering first one

## v1.4.2 (2015-03-01)
- Performance improvements

## v1.4.1 (2015-02-21)
- Performance improvements

## v1.4.0 (2015-02-09)
- Added ServletLoader which is the only built-in loader that works with JBoss/Wildfly
- Added "first" filter
- Added "last" filter
- Added "join" filter

## v1.3.1 (2015-01-24)
- Fixed lexing issue on windows
- Fixed number comparison issue

## v1.3.0 (2014-12-08)
- Added "filter" tag
- Added "abs" filter
- Added "sort" filter
- Pebble now uses the exact Map implementation provided by user instead of moving variables into it's own map implementation. This allows for custom "lazy" maps and other unique implementations.
- Arrays and lists can now be accessed by index

## v1.2.0 (2014-12-02)
- Added verbatim tag
- Removed the LocaleAware interface; filters/functions/tests now get the locale via the EvaluationContext that is passed as a "_context" argument in the argument map
- Added a whitespace control character: "-"
- Fixed bug where macros were being secretly evaluated one too many times

## v1.1.0 (2014-10-09)
- The ability to call bean methods that require arguments.
- For loop now works with primitive arrays (i.e. no longer just Iterable objects).
- Added "subscript syntax" support for accessing attributes.
- Continuous integration with travis-ci.
- Fixed NPE occurring in ternary expressions.
- Fixed issue with if-then-else expressions
- General code and testing improvements.

## v1.0.0 (2014-03-29)
- Some code cleanup and fixed an incorrect unit test.

## v0.4.0-beta (2014-03-05)
- No more code generation, all nodes of the AST are rendered during template evaluation phase.

## v0.3.0-beta (2014-02-22)
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
