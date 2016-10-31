package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.extension.ChainableBuilder;
import com.mitchellbosecke.pebble.node.expression.UnaryMinusExpression;
import com.mitchellbosecke.pebble.node.expression.UnaryNotExpression;
import com.mitchellbosecke.pebble.node.expression.UnaryPlusExpression;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperatorImpl;

import java.util.ArrayList;
import java.util.List;

public class UnaryOperatorBuilder extends ChainableBuilder<CoreExtension.Builder> {

    private boolean notOperator = true;
    private boolean plusOperator = true;
    private boolean minusOperator = true;

    public UnaryOperatorBuilder(CoreExtension.Builder builder) {
        super(builder);
    }

    public UnaryOperatorBuilder enableNotOperator() {
        notOperator = true;
        return this;
    }

    public UnaryOperatorBuilder disableNotOperator() {
        notOperator = false;
        return this;
    }

    public UnaryOperatorBuilder enablePlusOperator() {
        plusOperator = true;
        return this;
    }

    public UnaryOperatorBuilder disablePlusOperator() {
        plusOperator = false;
        return this;
    }

    public UnaryOperatorBuilder enableMinusOperator() {
        minusOperator = true;
        return this;
    }

    public UnaryOperatorBuilder disableMinusOperator() {
        minusOperator = false;
        return this;
    }

    public UnaryOperatorBuilder disableAll() {
        notOperator = false;
        plusOperator = false;
        minusOperator = false;

        return this;
    }

    public List<UnaryOperator> build() {
        List<UnaryOperator> unaryOperators = new ArrayList<>();

        if(notOperator) {
            unaryOperators.add(new UnaryOperatorImpl("not", 5, UnaryNotExpression.class));
        }
        if(plusOperator) {
            unaryOperators.add(new UnaryOperatorImpl("+", 500, UnaryPlusExpression.class));
        }
        if(minusOperator) {
            unaryOperators.add(new UnaryOperatorImpl("-", 500, UnaryMinusExpression.class));
        }

        return unaryOperators;
    }

}
