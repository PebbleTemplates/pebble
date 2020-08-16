---
---

# Extending Pebble

## Overview
Pebble was designed to be flexible and accomodate the requirements of any project. You can add your own tags,
functions, operators, filters, tests, and global variables. The majority of these are quite trivial to implement.

Begin by creating a class that implements `Extension`. For your own convenience, I recommend extending
`AbstractExtension` if you can. After implementing the required methods, register your extension with the `PebbleEngine`
before compiling any templates:
```java
PebbleEngine engine = new PebbleEngine.Builder().extension(new CustomExtension()).build();
```

## Filters
To create custom filters, implement the `getFilters()` method of your extension which will return a map of filter
names and their corresponding implementations. A filter implementation must implement the `Filter` interface.
The	`Filter` interface requires two methods to be implemented, `getArgumentNames()` and `apply()`. The
`getArgumentNames()` method returns a list of Strings that define both the order and names of expected arguments.

The `apply` method is the actual filter implementation. Here's the parameters definition.

| Parameter name | Description |
| --- | --- |
| input | the data to be filtered |
| args | the map of arguments the user may have provided |
| self | An instance of `PebbleTemplate` which can be used to retrieve the template name for example |
| context | An instance of `EvaluationContext` which can be used to retrieve the locale for example| 
| lineNumber | Useful when throwing exception to provide line number | 
 
Because Pebble is dynamically typed, you will have to downcast the arguments to the expected type.
Here is an example of how the {{ anchor('upper') }} filter might be implemented:
```java
public class UpperFilter implements Filter {

	@Override
	public List<String> getArgumentNames() {
		return null;
	}

	@Override
	public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber){
		if(input == null){
			return null;
		}
		if (input instanceof String) {
			return ((String) input).toUpperCase(context.getLocale());
		} else {
			return input.toString().toUpperCase(context.getLocale());
		}
	}

}
```

## Tests
Adding custom tests is very similar to custom filters. Implement the `getTests()` method within your
extension which will return a map of test names and their corresponding implementations. A test
implementation will implement the `Test` interface. The `Test` interface is exactly like the `Filter`
interface except the apply method returns a boolean instead of an arbitrary object of any type.

Here is an example of how the {{ anchor('even') }} test might be implemented:
```java
public class EvenTest implements Test {

	@Override
	public List<String> getArgumentNames() {
		return null;
	}

	@Override
	public boolean apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber){
		if (input == null) {
			throw new PebbleException(null, "Can not pass null value to \"even\" test.", lineNumber, self.getName());
		}
    
		if (input instanceof Integer) {
			return ((Integer) input) % 2 == 0;
		} else {
			return ((Long) input) % 2 == 0;
		}
	}

}
```

## Functions
Adding functions is also very similar to custom filters. First and foremost, it's important to
understand the different intentions behind a function and a filter because it can often be ambiguous
which one should be implemented. A filter is intended to modify existing content where a function is
moreso intended to produce new content.

To add functions, implement the `getFunctions()` method within your extension which will return a map of function
names and their corresponding implementations. A function implementation will implement the `Function` interface.
The	`Function` interface is very similar to the `Filter` and `Test` interfaces.

Here is an example of how a fictional `fibonacciString` function might be implemented:
```java
public class FibonnaciStringFunction implements Function {

	@Override
	public List<String> getArgumentNames() {
		List<String> names = new ArrayList<>();
		names.add("length");
		return names;
	}

	@Override
	public Object execute(Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) {
		Integer length = (Integer)args.get("length");
		Integer prev1 = 0;
		Integer prev2 = 1;

		StringBuilder result = new StringBuilder();

		result.append("01");

		for(int i = 2; i < length; i++){
			Integer next = prev1 + prev2;
			result.append(next);
			prev1 = prev2;
			prev2 = next;
		}
		return result.toString();

	}
}
```

## Positional and Named Arguments
For filters, tests, and functions it is required that you implement the `getArgumentNames` method even if it
returns null. Returning a list of strings will allow the end user to call your filter/test/function using
named arguments. Using the above fictional fibonacci function as an example, a user can invoke it in two different ways:
```twig
{% verbatim %}
{{ fibonacci(10) }}
{{ fibonacci(length=10) }}
{%- endverbatim %}
```
If the end user excludes the names and only uses positional arguments, the argument values will still end up
be mapped to the proper names when it's time to invoke the function's execute method. Your function implementation
doesn't have to worry whether the user used positional or named arguments. It is important though that if the
filter/function/test expects more than one argument, then the developer must communicate to the user the expected
order of arguments in the chance that the user wants to invoke it without using names.

Some functions such as the built in `min` and `max` functions accept an unlimited amount of arguments.
For this to happen, your function must not accept any named arguments (i.e. your `getArgumentNames` method
will return null or empty) and your `execute`` method will simply iterate over the values of the user provided
argument map while ignoring the keys of that map (Pebble will use arbitrary keys if there are no names to map to).

## Global Variables
Adding global variables, which are variables that are accessbile to all templates, is very trivial.
In your custom extension, implement the `getGlobalVariables()` method which returns a `Map<String,Object>`.
The contents of this map will be merged into the context you provide to each template at the time of rendering.

## Operators
Operators are more complex to implement than filters or tests. To add custom operators, implement the
`getBinaryOperators()` or the `getUnaryOperators()` method in your extension, or both. These methods return a
list of `BinaryOperator` or `UnaryOperator` objects, respectively.

Binary operators require the following information:
- Precedence: an integer relative to other operators which defines the order of operations.
- Symbol: a String representing the actual operator. This is typically a single character but doesn't have to be.
- Expression Class: A class that extends `BinaryExpression`. This class will perform the actual operator implementation.
- Associativity: Either left or right depending on how the operator is used.

A unary operator is much the same except it's expression class must extend `UnaryExpression` and there is no associativity.

The precedence values for existing core operators are as followed:
- `or`: 10
- `and`: 15
- `is`: 20
- `is not`: 20
- `==`: 30
- `!=`: 30
- `>`: 30
- `<`: 30
- `>=`: 30
- `<=`: 30
- `+`: 40
- `-`: 40
- `not`: 50 (Unary)
- `*`: 60
- `/`: 60
- `%`: 60
- `|`: 100
- `+`: 500 (Unary)
- `-`: 500 (Unary)

The following is an	example of how the addition operator (`+`) might have been implemented:
```java
public class AdditionOperator implements BinaryOperator {

	public int getPrecedence(){
		return 30;
	}

	public String getSymbol(){
		return "+";
	}

    public BinaryExpression<?> getInstance() {
        return new AddExpression();
    }

    public BinaryOperatorType getType() {
        return BinaryOperatorType.NORMAL;
    }

	public Associativity getAssociativity(){
		return Associativity.LEFT;
	}

}
```
Alongside each operator class you will also need to implement a corresponding `BinaryExpression` class
which actually implements the operator. The above example references a fictional `AdditionExpression` class
which might look like the following:
```java
public class AdditionExpression extends BinaryExpression<Object> {

	@Override
	public Object evaluate(PebbleTemplateImpl self, EvaluationContext context){
		Integer left = (Integer)getLeftExpression().evaluate(self, context);
		Integer right = (Integer)getRightExpression().evaluate(self, context);

		return left + right;
	}

}
```
In the above example you will notice that children of BinaryExpression have access to two other
expressions, `leftExpression`, and `rightExpression`; these are the operands of your operator.
Please note that in the above example both operands are casted to Integers but in reality you can't
always make that assumption; the true addition expression is much more complex to handle different
types of operands (Integers, Longs, Doubles, etc).

## Tags
Creating new tags is one of the most powerful abilities of Pebble. Your extension should start by
implementing the `getTokenParsers()` method. A `TokenParser` is responsible for converting all
necessary tokens to appropriate `RenderableNodes`. A token is a significant and irreducible
group of characters found in a template (such as an operator, whitespace, variable name, delimiter, etc)
and a `RenderableNode` is a Pebble class that is responsible for generating output.

Let us look at an example of a `TokenParser`:
```java
public class SetTokenParser implements TokenParser {

	public String getTag(){
		return "set";
	}

	@Override
	public RenderableNode parse(Token token, Parser parser) {
		TokenStream stream = parser.getStream();
		int lineNumber = token.getLineNumber();

		// skip the "set" token
		stream.next();

		// use the built in expression parser to parse the variable name
		String name = parser.getExpressionParser().parseNewVariableName();

		stream.expect(Token.Type.PUNCTUATION, "=");

		// use the built in expression parser to parse the variable value
		Expression<?> value = parser.getExpressionParser().parseExpression();

		// expect to see "%}"
		stream.expect(Token.Type.EXECUTE_END);

		// NodeSet is composed of a name and a value
		return new SetNode(lineNumber, name, value);
	}

}
```
The `getTag()` method must return the name of the tag. Pebble's main parser will use this name to determine
when to delegate responsibility to your custom `TokenParser`. This example is parsing the `set` tag.

The parse method is invoked whenever the primary parser encounters a set token. This method should return
one `RenderableNode` instance which when rendered during the template evaluation, will write output to the
provided Writer object. If the `RenderableNode` contains children nodes, it should invoke the render method
of those nodes as well.

The best way to learn all the details of parsing is to look at some of the tools used, as well as some examples.
Here is a list of classes I suggest reading:
- `TokenParser`
- `Parser`
- `SetTokenParser`
- `ForTokenParser`
- `IfNode`
- `SetNode`

## Attribute resolver (v3 only)
To create a new attribute resolver, implement the `getAttributeResolver()` method of your extension which will return a list of
attribute resolvers to run. A attribute resolver implementation must implement the `AttributeResolver` interface.
The	`AttributeResolver` interface requires one method to be implemented, `resolve()`.

The custom attribute resolver will be executed before all default pebble attribute resolvers. It replaces the
`DynamicAttributeProvider` interface

```java
public class DefaultAttributeResolver implements AttributeResolver {

  @Override
  public ResolvedAttribute resolve(Object instance,
                                   Object attributeNameValue,
                                   Object[] argumentValues,
                                   boolean isStrictVariables,
                                   String filename,
                                   int lineNumber) {
    if (instance instanceof CustomObject) {
      return "customValue";
    }
    return null;
  }
}
```
