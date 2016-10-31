package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.ChainableBuilder;
import com.mitchellbosecke.pebble.node.expression.*;
import com.mitchellbosecke.pebble.operator.Associativity;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.BinaryOperatorImpl;

import java.util.ArrayList;
import java.util.List;

public class BinaryOperatorBuilder extends ChainableBuilder<CoreExtension.Builder> {

    private boolean or = true;
    private boolean and = true;
    private boolean positiveTest = true;
    private boolean negativeTest = true;
    private boolean contains = true;
    private boolean equals = true;
    private boolean equalsWord = true;
    private boolean notEquals = true;
    private boolean greaterThan = true;
    private boolean lessThan = true;
    private boolean greaterThanEquals = true;
    private boolean lessThanEquals = true;
    private boolean add = true;
    private boolean subtract = true;
    private boolean multiply = true;
    private boolean divide = true;
    private boolean modulus = true;
    private boolean filter = true;
    private boolean concatenate = true;
    private boolean range = true;

    public BinaryOperatorBuilder(CoreExtension.Builder builder) {
        super(builder);
    }

    public BinaryOperatorBuilder enableOr() {
        or = true;
        return this;
    }

    public BinaryOperatorBuilder enableAnd() {
        and = true;
        return this;
    }

    public BinaryOperatorBuilder enablePositiveTest() {
        positiveTest = true;
        return this;
    }

    public BinaryOperatorBuilder enableNegativeTest() {
        negativeTest = true;
        return this;
    }

    public BinaryOperatorBuilder enableContains() {
        contains = true;
        return this;
    }

    public BinaryOperatorBuilder enableEquals() {
        equals = true;
        return this;
    }

    public BinaryOperatorBuilder enableEqualsWord() {
        equalsWord = true;
        return this;
    }

    public BinaryOperatorBuilder enableNotEquals() {
        notEquals = true;
        return this;
    }

    public BinaryOperatorBuilder enableGreaterThan() {
        greaterThan = true;
        return this;
    }

    public BinaryOperatorBuilder enableLessThan() {
        lessThan = true;
        return this;
    }

    public BinaryOperatorBuilder enableGreaterThanEquals() {
        greaterThanEquals = true;
        return this;
    }

    public BinaryOperatorBuilder enableLessThanEquals() {
        lessThanEquals = true;
        return this;
    }

    public BinaryOperatorBuilder enableAdd() {
        add = true;
        return this;
    }

    public BinaryOperatorBuilder enableSubtract() {
        subtract = true;
        return this;
    }

    public BinaryOperatorBuilder enableMultiply() {
        multiply = true;
        return this;
    }

    public BinaryOperatorBuilder enableDivide() {
        divide = true;
        return this;
    }

    public BinaryOperatorBuilder enableModulus() {
        modulus = true;
        return this;
    }

    public BinaryOperatorBuilder enableFilter() {
        filter = true;
        return this;
    }

    public BinaryOperatorBuilder enableConcatenate() {
        concatenate = true;
        return this;
    }

    public BinaryOperatorBuilder enableRange() {
        range = true;
        return this;
    }

    public BinaryOperatorBuilder disableOr() {
        or = false;
        return this;
    }

    public BinaryOperatorBuilder disableAnd() {
        and = false;
        return this;
    }

    public BinaryOperatorBuilder disablePositiveTest() {
        positiveTest = false;
        return this;
    }

    public BinaryOperatorBuilder disableNegativeTest() {
        negativeTest = false;
        return this;
    }

    public BinaryOperatorBuilder disableContains() {
        contains = false;
        return this;
    }

    public BinaryOperatorBuilder disableEquals() {
        equals = false;
        return this;
    }

    public BinaryOperatorBuilder disableEqualsWord() {
        equalsWord = false;
        return this;
    }

    public BinaryOperatorBuilder disableNotEquals() {
        notEquals = false;
        return this;
    }

    public BinaryOperatorBuilder disableGreaterThan() {
        greaterThan = false;
        return this;
    }

    public BinaryOperatorBuilder disableLessThan() {
        lessThan = false;
        return this;
    }

    public BinaryOperatorBuilder disableGreaterThanEquals() {
        greaterThanEquals = false;
        return this;
    }

    public BinaryOperatorBuilder disableLessThanEquals() {
        lessThanEquals = false;
        return this;
    }

    public BinaryOperatorBuilder disableAdd() {
        add = false;
        return this;
    }

    public BinaryOperatorBuilder disableSubtract() {
        subtract = false;
        return this;
    }

    public BinaryOperatorBuilder disableMultiply() {
        multiply = false;
        return this;
    }

    public BinaryOperatorBuilder disableDivide() {
        divide = false;
        return this;
    }

    public BinaryOperatorBuilder disableModulus() {
        modulus = false;
        return this;
    }

    public BinaryOperatorBuilder disableFilter() {
        filter = false;
        return this;
    }

    public BinaryOperatorBuilder disableConcatenate() {
        concatenate = false;
        return this;
    }

    public BinaryOperatorBuilder disableRange() {
        range = false;
        return this;
    }

    public BinaryOperatorBuilder disableAll() {
        or = false;
        and = false;
        positiveTest = false;
        negativeTest = false;
        contains = false;
        equals = false;
        equalsWord = false;
        notEquals = false;
        greaterThan = false;
        lessThan = false;
        greaterThanEquals = false;
        lessThanEquals = false;
        add = false;
        subtract = false;
        multiply = false;
        divide = false;
        modulus = false;
        filter = false;
        concatenate = false;
        range = false;

        return this;
    }

    public List<BinaryOperator> build(){
        List<BinaryOperator> binaryOperators = new ArrayList<>();

        if(or) {
            binaryOperators.add(new BinaryOperatorImpl("or", 10, OrExpression.class, Associativity.LEFT));
        }
        if(and) {
            binaryOperators.add(new BinaryOperatorImpl("and", 15, AndExpression.class, Associativity.LEFT));
        }
        if(positiveTest) {
            binaryOperators.add(new BinaryOperatorImpl("is", 20, PositiveTestExpression.class, Associativity.LEFT));
        }
        if(negativeTest) {
            binaryOperators.add(new BinaryOperatorImpl("is not", 20, NegativeTestExpression.class, Associativity.LEFT));
        }
        if(contains) {
            binaryOperators.add(new BinaryOperatorImpl("contains", 20, ContainsExpression.class, Associativity.LEFT));
        }
        if(equals) {
            binaryOperators.add(new BinaryOperatorImpl("==", 30, EqualsExpression.class, Associativity.LEFT));
        }
        if(equalsWord) {
            binaryOperators.add(new BinaryOperatorImpl("equals", 30, EqualsExpression.class, Associativity.LEFT));
        }
        if(notEquals) {
            binaryOperators.add(new BinaryOperatorImpl("!=", 30, NotEqualsExpression.class, Associativity.LEFT));
        }
        if(greaterThan) {
            binaryOperators.add(new BinaryOperatorImpl(">", 30, GreaterThanExpression.class, Associativity.LEFT));
        }
        if(lessThan) {
            binaryOperators.add(new BinaryOperatorImpl("<", 30, LessThanExpression.class, Associativity.LEFT));
        }
        if(greaterThanEquals) {
            binaryOperators.add(new BinaryOperatorImpl(">=", 30, GreaterThanEqualsExpression.class, Associativity.LEFT));
        }
        if(lessThanEquals) {
            binaryOperators.add(new BinaryOperatorImpl("<=", 30, LessThanEqualsExpression.class, Associativity.LEFT));
        }
        if(add) {
            binaryOperators.add(new BinaryOperatorImpl("+", 40, AddExpression.class, Associativity.LEFT));
        }
        if(subtract) {
            binaryOperators.add(new BinaryOperatorImpl("-", 40, SubtractExpression.class, Associativity.LEFT));
        }
        if(multiply) {
            binaryOperators.add(new BinaryOperatorImpl("*", 60, MultiplyExpression.class, Associativity.LEFT));
        }
        if(divide) {
            binaryOperators.add(new BinaryOperatorImpl("/", 60, DivideExpression.class, Associativity.LEFT));
        }
        if(modulus) {
            binaryOperators.add(new BinaryOperatorImpl("%", 60, ModulusExpression.class, Associativity.LEFT));
        }
        if(filter) {
            binaryOperators.add(new BinaryOperatorImpl("|", 100, FilterExpression.class, Associativity.LEFT));
        }
        if(concatenate) {
            binaryOperators.add(new BinaryOperatorImpl("~", 110, ConcatenateExpression.class, Associativity.LEFT));
        }
        if(range) {
            binaryOperators.add(new BinaryOperatorImpl("..", 120, RangeExpression.class, Associativity.LEFT));
        }

        return binaryOperators;
    }
}
