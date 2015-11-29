package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.operator.BinaryOperator;
import com.mitchellbosecke.pebble.operator.UnaryOperator;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;

import java.util.*;

/**
 * Storage for the extensions and the components retrieved
 * from the various extensions.
 * <p>
 * Created by mitch_000 on 2015-11-28.
 */
public class ExtensionRegistry {

    /**
     * Extensions
     */
    private HashMap<Class<? extends Extension>, Extension> extensions = new HashMap<>();

    /**
     * Unary operators used during the lexing phase.
     */
    private Map<String, UnaryOperator> unaryOperators = new HashMap<>();

    /**
     * Binary operators used during the lexing phase.
     */
    private Map<String, BinaryOperator> binaryOperators = new HashMap<>();

    /**
     * Token parsers used during the parsing phase.
     */
    private Map<String, TokenParser> tokenParsers = new HashMap<>();

    /**
     * Node visitors available during the parsing phase.
     */
    private List<NodeVisitorFactory> nodeVisitors = new ArrayList<>();

    /**
     * Filters used during the evaluation phase.
     */
    private Map<String, Filter> filters = new HashMap<>();

    /**
     * Tests used during the evaluation phase.
     */
    private Map<String, Test> tests = new HashMap<>();

    /**
     * Functions used during the evaluation phase.
     */
    private Map<String, Function> functions = new HashMap<>();

    /**
     * Global variables available during the evaluation phase.
     */
    private Map<String, Object> globalVariables = new HashMap<>();

    public ExtensionRegistry(Collection<? extends Extension> extensions) {

        for (Extension extension : extensions) {
            this.extensions.put(extension.getClass(), extension);

            // token parsers
            List<TokenParser> tokenParsers = extension.getTokenParsers();
            if (tokenParsers != null) {
                for (TokenParser tokenParser : tokenParsers) {
                    this.tokenParsers.put(tokenParser.getTag(), tokenParser);
                }
            }

            // binary operators
            List<BinaryOperator> binaryOperators = extension.getBinaryOperators();
            if (binaryOperators != null) {
                for (BinaryOperator operator : binaryOperators) {
                    if (!this.binaryOperators.containsKey(operator.getSymbol())) { // disallow overriding core operators
                        this.binaryOperators.put(operator.getSymbol(), operator);
                    }
                }
            }

            // unary operators
            List<UnaryOperator> unaryOperators = extension.getUnaryOperators();
            if (unaryOperators != null) {
                for (UnaryOperator operator : unaryOperators) {
                    if (!this.unaryOperators.containsKey(operator.getSymbol())) { // disallow override core operators
                        this.unaryOperators.put(operator.getSymbol(), operator);
                    }
                }
            }

            // filters
            Map<String, Filter> filters = extension.getFilters();
            if (filters != null) {
                this.filters.putAll(filters);
            }

            // tests
            Map<String, Test> tests = extension.getTests();
            if (tests != null) {
                this.tests.putAll(tests);
            }

            // tests
            Map<String, Function> functions = extension.getFunctions();
            if (functions != null) {
                this.functions.putAll(functions);
            }

            // global variables
            Map<String, Object> globalVariables = extension.getGlobalVariables();
            if (globalVariables != null) {
                this.globalVariables.putAll(globalVariables);
            }

            // node visitors
            List<NodeVisitorFactory> nodeVisitors = extension.getNodeVisitors();
            if (nodeVisitors != null) {
                this.nodeVisitors.addAll(nodeVisitors);
            }
        }
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

    /*
    @SuppressWarnings("unchecked")
    public <T extends Extension> T getExtension(Class<T> clazz) {
        return (T) this.extensions.get(clazz);
    }



    public HashMap<Class<? extends Extension>, Extension> getExtensions() {
        return extensions;
    }
    */
}
