---
---

# Contributing

## Help Wanted
Contributors of all types are welcome but most importantly I am looking for help with the following:

- IDE Integration
- API Feedback
- Testing and Bug Reports
- Performance Optimizations
- Improving Thread Safety
General improvements are welcome, otherwise you can help tackle some of the [known issues](https://github.com/PebbleTemplates/pebble/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc).

## Getting Started
- Fork the [repository](https://github.com/PebbleTemplates/pebble)
- Make the fix.
- Use maven to build and test:
    - `mvn clean install` from root to build Pebble
    - `mvn orchid:serve -P doc` from `docs/` to build and serve documentation on http://localhost:8080
- Submit a pull request

## Understanding the Code
There are a few major components that play a vital role in the compilation process. The main
`PebbleEngine`, the `LexerImpl`, and the `ParserImpl`.

The `PebbleEngine` is responsible for beginning the template compilation process. It begins by passing
the template source to the `Lexer`.

The `Lexer` is responsible for converting the template into a bunch of `Token` objects. A token is the smallest
distinguishable unit in a template, i.e. it can not be broken down into more specific objects. A token can
represent a delimiter (eg. `{{ '{{' }}` ), or a function name, a string, plain text, or many other things. Once the
lexer has established the entire stream of tokens which collectively make up the entire template, it returns
this `TokenStream` back to the main `PebbleEngine`.

The `PebbleEngine` now sends the `TokenStream` to the `Parser` which is responsible for turning those token objects
into `Node` objects. The nodes are built in a hierarchical fashion and together they make up the abstract
syntax tree. The parser uses the help of the main `ExpressionParser` as well as the `TokenParser` objects which
are provided by the	extensions. Each node is responsible for rendering itself during the template
evaluation phase.

The `PebbleEngine` now has a tree of node objects, beginning with the `NodeRoot`. It will then create a
`PebbleTemplateImpl` object with this root node as an argument. The last step before returning this template
to the user is to invoke both the internal and the user-provided `NodeVisitor` objects on the node tree.
Node visitors are used to process the node tree, perhaps to add extra functionality (ex. the built-in
autoescaper uses a node visitor to wrap the expressions of nodes with an escape function). Once this
processing is completed, the template is placed into the cache and finally returned to the user.

The user will then call `evaluate()` on this template which begins the evaluation process. The template
will call `render()` on it's root node which in turn will call `render()` on each of it's children nodes.
This process recurses throughout the whole node tree until all nodes have rendered themselves to the
provided `Writer` object.

## Documentation

The documentation website is generated using [Orchid](https://orchid.netlify.com/). 

The documentation files are under the `src/orchid/resources` directory.

## Contributing Code
Currently there aren't any formal guidelines. Just ensure that your changes include any
necessary unit tests and send me a pull request on [github](https://github.com/PebbleTemplates/pebble)!

## Standards and Guidelines

### Code Formatting Standards

1. Use [Google Style Guide](https://google.github.io/styleguide/javaguide.html) formatting conventions.
   1. Configuring IntelliJ to use the Google Style Guide Formatter.
      1. Clone the [Google Style Git Repo](https://github.com/google/styleguide).
      2. Open **Preferences -> Editor -> Code Style -> Java**. 
      4. Click on the gear and select **Import Scheme -> IntelliJ IDEA code style XML** and import the file **intellij-java-google-style.xml** from the [Google Style Git Repo](https://github.com/google/styleguide).
   2. Configuring Eclipse to use the Google Style Guide Formatter.
      1. Clone the [Google Style Git Repo](https://github.com/google/styleguide).
      2. Select **Preference -> Java -> Code Style -> Formatter**.
      3. Select **Import** and specify the file **eclipse-java-google-style.xml** from the [Google Style Git Repo](https://github.com/google/styleguide).

### Unit Test Guidelines

1. Use [AssertJ]() for all Unit and Integration tests. 
2. A javadoc description should be included for each test method.
3. The purpose of each test should be clear from the test method name.

## Acknowledgements
Thanks to all the following contributors who are helping to make Pebble the best template engine available for Java. 

{%- for contributor in data.contributors %}
* [{{ contributor.name }}]({{ contributor.link }})
{%- endfor %}
