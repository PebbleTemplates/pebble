package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

import java.util.*;

/**
 * Storage for the extensions and the components retrieved from the various
 * extensions.
 * <p>
 * Created by mitch_000 on 2015-11-28.
 */
public class ExtensionRegistry {

    /**
     * Extensions
     */
    private final Map<Class<? extends Extension>, Extension> extensions;

    /**
     * Unary operators used during the lexing phase.
     */
    private final Map<String, UnaryOperator> unaryOperators;

    /**
     * Binary operators used during the lexing phase.
     */
    private final Map<String, BinaryOperator> binaryOperators;

    /**
     * Token parsers used during the parsing phase.
     */
    private final Map<String, TokenParser> tokenParsers;

    /**
     * Node visitors available during the parsing phase.
     */
    private final List<NodeVisitorFactory> nodeVisitors;

    /**
     * Filters used during the evaluation phase.
     */
    private final Map<String, Filter> filters;

    /**
     * Tests used during the evaluation phase.
     */
    private final Map<String, Test> tests;

    /**
     * Functions used during the evaluation phase.
     */
    private final Map<String, Function> functions;

    /**
     * Global variables available during the evaluation phase.
     */
    private final Map<String, Object> globalVariables;

    public ExtensionRegistry(Collection<? extends Extension> extensions) {

        final HashMap<Class<? extends Extension>, Extension> extensionsMap = new HashMap<>();
        final Map<String, TokenParser> tokenParsersMap = new HashMap<>();
        final Map<String, UnaryOperator> unaryOperatorsMap = new HashMap<>();
        final Map<String, BinaryOperator> binaryOperatorsMap = new HashMap<>();
        final List<NodeVisitorFactory> nodeVisitorsMap = new ArrayList<>();
        final Map<String, Filter> filtersList = new HashMap<>();
        final Map<String, Test> testsMap = new HashMap<>();
        final Map<String, Function> functionsMap = new HashMap<>();
        final Map<String, Object> globalVariablesMap = new HashMap<>();

        for (Extension extension : extensions) {
            extensionsMap.put(extension.getClass(), extension);

            // token parsers
            List<TokenParser> tokenParsers = extension.getTokenParsers();
            if (tokenParsers != null) {
                for (TokenParser tokenParser : tokenParsers) {
                    tokenParsersMap.put(tokenParser.getTag(), tokenParser);
                }
            }

            // binary operators
            List<BinaryOperator> binaryOperators = extension.getBinaryOperators();
            if (binaryOperators != null) {
                for (BinaryOperator operator : binaryOperators) {
                    if (!binaryOperatorsMap.containsKey(operator.getSymbol())) { // disallow
                                                                                 // overriding
                                                                                 // core
                                                                                 // operators
                        binaryOperatorsMap.put(operator.getSymbol(), operator);
                    }
                }
            }

            // unary operators
            List<UnaryOperator> unaryOperators = extension.getUnaryOperators();
            if (unaryOperators != null) {
                for (UnaryOperator operator : unaryOperators) {
                    if (!unaryOperatorsMap.containsKey(operator.getSymbol())) { // disallow
                                                                                // override
                                                                                // core
                                                                                // operators
                        unaryOperatorsMap.put(operator.getSymbol(), operator);
                    }
                }
            }

            // filters
            Map<String, Filter> filters = extension.getFilters();
            if (filters != null) {
                filtersList.putAll(filters);
            }

            // tests
            Map<String, Test> tests = extension.getTests();
            if (tests != null) {
                testsMap.putAll(tests);
            }

            // tests
            Map<String, Function> functions = extension.getFunctions();
            if (functions != null) {
                functionsMap.putAll(functions);
            }

            // global variables
            Map<String, Object> globalVariables = extension.getGlobalVariables();
            if (globalVariables != null) {
                globalVariablesMap.putAll(globalVariables);
            }

            // node visitors
            List<NodeVisitorFactory> nodeVisitors = extension.getNodeVisitors();
            if (nodeVisitors != null) {
                nodeVisitorsMap.addAll(nodeVisitors);
            }
        }
        this.extensions = Collections.unmodifiableMap(extensionsMap);
        this.tokenParsers = Collections.unmodifiableMap(tokenParsersMap);
        this.unaryOperators = Collections.unmodifiableMap(unaryOperatorsMap);
        this.binaryOperators = Collections.unmodifiableMap(binaryOperatorsMap);
        this.nodeVisitors = Collections.unmodifiableList(nodeVisitorsMap);
        this.filters = Collections.unmodifiableMap(filtersList);
        this.tests = Collections.unmodifiableMap(testsMap);
        this.functions = Collections.unmodifiableMap(functionsMap);
        this.globalVariables = Collections.unmodifiableMap(globalVariablesMap);

    }

    public Map<Class<? extends Extension>, Extension> getExtensions() {
        return extensions;
    }

    public Map<String, Filter> getFilters() {
        return filters;
    }

    public Map<String, Function> getFunctions() {
        return functions;
    }

    public Map<String, Test> getTests() {
        return tests;
    }

    public Filter getFilter(String name) {
        return this.filters.get(name);
    }

    public Test getTest(String name) {
        return this.tests.get(name);
    }

    public Function getFunction(String name) {
        return this.functions.get(name);
    }

    public Map<String, BinaryOperator> getBinaryOperators() {
        return this.binaryOperators;
    }

    public Map<String, UnaryOperator> getUnaryOperators() {
        return this.unaryOperators;
    }

    public List<NodeVisitorFactory> getNodeVisitors() {
        return this.nodeVisitors;
    }

    public Map<String, Object> getGlobalVariables() {
        return this.globalVariables;
    }

    public Map<String, TokenParser> getTokenParsers() {
        return this.tokenParsers;
    }
}
