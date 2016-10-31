/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.extension.core;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.*;
import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

import java.util.List;
import java.util.Map;

public class CoreExtension extends AbstractExtension {

    private final List<TokenParser> tokenParsers;

    private final List<UnaryOperator> unaryOperators;

    private final List<BinaryOperator> binaryOperators;

    private final Map<String, Filter> filters;

    private final Map<String, Test> tests;

    private final Map<String, Function> functions;

    private final List<NodeVisitorFactory> nodeVisitorFactories;

    private CoreExtension(
            List<TokenParser> tokenParsers,
            List<UnaryOperator> unaryOperators,
            List<BinaryOperator> binaryOperators,
            Map<String, Filter> filters,
            Map<String, Test> tests,
            Map<String, Function> functions,
            List<NodeVisitorFactory> nodeVisitorFactories
            ){

        this.tokenParsers = tokenParsers;
        this.unaryOperators = unaryOperators;
        this.binaryOperators = binaryOperators;
        this.filters = filters;
        this.tests = tests;
        this.functions = functions;
        this.nodeVisitorFactories = nodeVisitorFactories;
    }

    @Override
    public List<TokenParser> getTokenParsers() {
        return tokenParsers;
    }

    @Override
    public List<UnaryOperator> getUnaryOperators() {
        return unaryOperators;
    }

    @Override
    public List<BinaryOperator> getBinaryOperators() {
        return binaryOperators;
    }

    @Override
    public Map<String, Filter> getFilters() {
        return filters;
    }

    @Override
    public Map<String, Test> getTests() {
        return tests;
    }

    @Override
    public Map<String, Function> getFunctions() {
        return functions;
    }

    @Override
    public Map<String, Object> getGlobalVariables() {
        return null;
    }

    @Override
    public List<NodeVisitorFactory> getNodeVisitors() {
        return nodeVisitorFactories;
    }

    /**
     * This {@link Builder} is used to enable/disable the default CoreExtensions
     */
    public static class Builder extends ChainableBuilder<PebbleEngine.Builder> {

        private boolean enabled = true;

        private TokenParserBuilder tokenParserBuilder;

        private UnaryOperatorBuilder unaryOperatorBuilder;

        private BinaryOperatorBuilder binaryOperatorBuilder;

        private FilterBuilder filterBuilder;

        private TestBuilder testBuilder;

        private FunctionBuilder functionBuilder;

        private NodeVisitorsBuilder nodeVisitorsBuilder;

        /**
         * @param builder an instance of {@link PebbleEngine.Builder} that will be returned
         *                when calling Builder{@link #and()}
         */
        public Builder(PebbleEngine.Builder builder){
            super(builder);
        }

        /**
         * this method enables any {@link CoreExtension} functionality
         *
         * @return the {@link Builder} itself
         */
        public Builder enable(){
            enabled = true;
            return this;
        }

        /**
         * this method disables any {@link CoreExtension} functionality
         *
         * @return the {@link Builder} itself
         */
        public Builder disable(){
            enabled = false;
            return this;
        }

        /**
         * retrieve a {@link TokenParserBuilder} to customize which {@link TokenParser} will be configured
         *
         * @return a {@link TokenParserBuilder} to work with
         */
        public TokenParserBuilder tokenParsers() {
            if(tokenParserBuilder == null) {
                tokenParserBuilder = new TokenParserBuilder(this);
            }
            return tokenParserBuilder;
        }

        /**
         * retrieve a {@link UnaryOperatorBuilder} to customize which {@link UnaryOperator} will be configured
         *
         * @return a {@link UnaryOperatorBuilder} to work with
         */
        public UnaryOperatorBuilder unaryOperators() {
            if(unaryOperatorBuilder == null) {
                unaryOperatorBuilder = new UnaryOperatorBuilder(this);
            }
            return unaryOperatorBuilder;
        }

        /**
         * retrieve a {@link BinaryOperatorBuilder} to customize which {@link BinaryOperator} will be configured
         *
         * @return a {@link BinaryOperatorBuilder} to work with
         */
        public BinaryOperatorBuilder binaryOperators() {
            if(binaryOperatorBuilder == null) {
                binaryOperatorBuilder = new BinaryOperatorBuilder(this);
            }
            return binaryOperatorBuilder;
        }

        /**
         * retrieve a {@link FilterBuilder} to customize which {@link Filter} will be configured
         *
         * @return a {@link FilterBuilder} to work with
         */
        public FilterBuilder filters() {
            if(filterBuilder == null) {
                filterBuilder = new FilterBuilder(this);
            }
            return filterBuilder;
        }

        /**
         * retrieve a {@link TestBuilder} to customize which {@link Test} will be configured
         *
         * @return a {@link TestBuilder} to work with
         */
        public TestBuilder tests() {
            if(testBuilder == null) {
                testBuilder = new TestBuilder(this);
            }
            return testBuilder;
        }

        /**
         * retrieve a {@link FunctionBuilder} to customize which {@link Function} will be configured
         *
         * @return a {@link FunctionBuilder} to work with
         */
        public FunctionBuilder functions() {
            if(functionBuilder == null) {
                functionBuilder = new FunctionBuilder(this);
            }
            return functionBuilder;
        }

        /**
         * retrieve a {@link NodeVisitorsBuilder} to customize which {@link NodeVisitorFactory} will be configured
         *
         * @return a {@link NodeVisitorsBuilder} to work with
         */
        public NodeVisitorsBuilder nodeVisitors() {
            if(nodeVisitorsBuilder == null) {
                nodeVisitorsBuilder = new NodeVisitorsBuilder(this);
            }
            return nodeVisitorsBuilder;
        }


        /**
         * this methods builds the {@link CoreExtension} according to the
         * configuration. If the extension was disabled it'll return an
         * instance of {@link NoOpExtension}.
         *
         * @return either an {@link CoreExtension} or a {@link NoOpExtension}
         */
        public Extension build(){
            if(enabled){
                return new CoreExtension(
                        tokenParsers().build(),
                        unaryOperators().build(),
                        binaryOperators().build(),
                        filters().build(),
                        tests().build(),
                        functions().build(),
                        nodeVisitors().build()
                );
            }else{
                return new NoOpExtension();
            }
        }

    }

}
