/*******************************************************************************
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.RootNode;
import com.mitchellbosecke.pebble.utils.FutureWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The actual implementation of a PebbleTemplate
 */
public class PebbleTemplateImpl implements PebbleTemplate {

    /**
     * A template has to store a reference to the main engine so that it can
     * compile other templates when using the "import" or "include" tags.
     * <p>
     * It will also retrieve some stateful information such as the default locale
     * when necessary. Luckily, the engine is immutable so this should be thread safe.
     */
    private final PebbleEngine engine;

    /**
     * Blocks defined inside this template.
     */
    private final Map<String, Block> blocks = new HashMap<>();

    /**
     * Macros defined inside this template.
     */
    private final Map<String, Macro> macros = new HashMap<>();

    /**
     * The root node of the AST to be rendered.
     */
    private final RootNode rootNode;

    /**
     * Name of template. Used to help with debugging.
     */
    private final String name;

    /**
     * Constructor
     *
     * @param engine The pebble engine used to construct this template
     * @param root   The root not to evaluate
     * @param name   The name of the template
     */
    public PebbleTemplateImpl(PebbleEngine engine, RootNode root, String name) {
        this.engine = engine;
        this.rootNode = root;
        this.name = name;
    }

    public void evaluate(Writer writer) throws PebbleException, IOException {
        EvaluationContext context = initContext(null);
        evaluate(writer, context);
    }

    public void evaluate(Writer writer, Locale locale) throws PebbleException, IOException {
        EvaluationContext context = initContext(locale);
        evaluate(writer, context);
    }

    public void evaluate(Writer writer, Map<String, Object> map) throws PebbleException, IOException {
        EvaluationContext context = initContext(null);
        context.getScopeChain().pushScope(map);
        evaluate(writer, context);
    }

    public void evaluate(Writer writer, Map<String, Object> map, Locale locale) throws PebbleException, IOException {
        EvaluationContext context = initContext(locale);
        context.getScopeChain().pushScope(map);
        evaluate(writer, context);
    }

    /**
     * This is the authoritative evaluate method. It will evaluate the template
     * starting at the root node.
     *
     * @param writer  The writer used to write the final output of the template
     * @param context The evaluation context
     * @throws PebbleException Thrown if any sort of template error occurs
     * @throws IOException     Thrown from the writer object
     */
    private void evaluate(Writer writer, EvaluationContext context) throws PebbleException, IOException {
        if (context.getExecutorService() != null) {
            writer = new FutureWriter(writer);
        }
        rootNode.render(this, writer, context);

        /*
         * If the current template has a parent then we know the current template
         * was only used to evaluate a very small subset of tags such as "set" and "import".
         * We now evaluate the parent template as to evaluate all of the actual content.
         * When evaluating the parent template, it will check the child template for overridden blocks.
         */
        if (context.getHierarchy().getParent() != null) {
            PebbleTemplateImpl parent = context.getHierarchy().getParent();
            context.getHierarchy().ascend();
            parent.evaluate(writer, context);
        }
        writer.flush();
    }

    /**
     * Initializes the evaluation context with settings from the engine.
     *
     * @param locale The desired locale
     * @return The evaluation context
     */
    private EvaluationContext initContext(Locale locale) {
        locale = locale == null ? engine.getDefaultLocale() : locale;

        // globals
        Map<String, Object> globals = new HashMap<>();
        globals.put("locale", locale);
        globals.put("template", this);
        ScopeChain scopeChain = new ScopeChain(globals);

        // global vars provided from extensions
        scopeChain.pushScope(new HashMap<>(engine.getExtensionRegistry().getGlobalVariables()));

        EvaluationContext context = new EvaluationContext(this, engine.isStrictVariables(), locale,
                engine.getExtensionRegistry(), engine.getTagCache(), engine.getExecutorService(),
                new ArrayList<PebbleTemplateImpl>(), scopeChain, null);
        return context;
    }

    /**
     * Imports a template.
     *
     * @param context The evaluation context
     * @param name    The template name
     * @throws PebbleException Thrown if an error occurs while rendering the imported
     *                         template
     */
    public void importTemplate(EvaluationContext context, String name) throws PebbleException {
        context.getImportedTemplates().add((PebbleTemplateImpl) engine.getTemplate(this.resolveRelativePath(name)));
    }

    /**
     * Includes a template with {@code name} into this template.
     *
     * @param writer              the writer to which the output should be written to.
     * @param context             the context within which the template is rendered in.
     * @param name                the name of the template to include.
     * @param additionalVariables the map with additional variables provided with the include
     *                            tag to add within the include tag.
     * @throws PebbleException Any error occurring during the compilation of the template
     * @throws IOException     Any error during the loading of the template
     */
    public void includeTemplate(Writer writer, EvaluationContext context, String name, Map<?, ?> additionalVariables)
            throws PebbleException, IOException {
        PebbleTemplateImpl template = (PebbleTemplateImpl) engine.getTemplate(this.resolveRelativePath(name));
        EvaluationContext newContext = context.shallowCopyWithoutInheritanceChain(template);
        ScopeChain scopeChain = newContext.getScopeChain();
        scopeChain.pushScope();
        for (Entry<?, ?> entry : additionalVariables.entrySet()) {
            scopeChain.put((String) entry.getKey(), entry.getValue());
        }
        template.evaluate(writer, newContext);
        scopeChain.popScope();
    }

    /**
     * Checks if a macro exists
     *
     * @param macroName The name of the macro
     * @return Whether or not the macro exists
     */
    public boolean hasMacro(String macroName) {
        return macros.containsKey(macroName);
    }

    /**
     * This method resolves the given relative path based on this template file
     * path.
     *
     * @param relativePath the path which should be resolved.
     * @return the resolved path.
     */
    public String resolveRelativePath(String relativePath) {
        String resolved = this.engine.getLoader().resolveRelativePath(relativePath, this.name);
        if (resolved == null) {
            return relativePath;
        } else {
            return resolved;
        }
    }

    /**
     * Registers a block.
     *
     * @param block The block
     */
    public void registerBlock(Block block) {
        blocks.put(block.getName(), block);
    }

    /**
     * Registers a macro
     *
     * @param macro The macro
     * @throws PebbleException Throws exception if macro already exists with the same name
     */
    public void registerMacro(Macro macro) throws PebbleException {
        if (macros.containsKey(macro.getName())) {
            throw new PebbleException(null, "More than one macro can not share the same name: " + macro.getName());
        }
        this.macros.put(macro.getName(), macro);
    }

    /**
     * A typical block declaration will use this method which evaluates the
     * block using the regular user-provided writer.
     *
     * @param blockName       The name of the block
     * @param context         The evaluation context
     * @param ignoreOverriden Whether or not to ignore overriden blocks
     * @param writer          The writer
     * @throws PebbleException Thrown if an error occurs
     * @throws IOException     Thrown from the writer object
     */
    public void block(Writer writer, EvaluationContext context, String blockName, boolean ignoreOverriden)
            throws PebbleException, IOException {

        Hierarchy hierarchy = context.getHierarchy();
        PebbleTemplateImpl childTemplate = hierarchy.getChild();

        // check child
        if (!ignoreOverriden && childTemplate != null) {
            hierarchy.descend();
            childTemplate.block(writer, context, blockName, false);
            hierarchy.ascend();

            // check this template
        } else if (blocks.containsKey(blockName)) {
            Block block = blocks.get(blockName);
            block.evaluate(this, writer, context);

            // delegate to parent
        } else {
            if (hierarchy.getParent() != null) {
                PebbleTemplateImpl parent = hierarchy.getParent();
                hierarchy.ascend();
                parent.block(writer, context, blockName, true);
                hierarchy.descend();
            }
        }

    }

    /**
     * Invokes a macro
     *
     * @param context         The evaluation context
     * @param macroName       The name of the macro
     * @param args            The arguments
     * @param ignoreOverriden Whether or not to ignore macro definitions in child template
     * @return The results of the macro invocation
     * @throws PebbleException An exception that may have occurred
     */
    public SafeString macro(EvaluationContext context, String macroName, ArgumentsNode args, boolean ignoreOverriden)
            throws PebbleException {
        SafeString result = null;
        boolean found = false;

        PebbleTemplateImpl childTemplate = context.getHierarchy().getChild();

        // check child template first
        if (!ignoreOverriden && childTemplate != null) {
            found = true;
            context.getHierarchy().descend();
            result = childTemplate.macro(context, macroName, args, false);
            context.getHierarchy().ascend();

            // check current template
        } else if (hasMacro(macroName)) {
            found = true;
            Macro macro = macros.get(macroName);

            Map<String, Object> namedArguments = args.getArgumentMap(this, context, macro);
            result = new SafeString(macro.call(this, context, namedArguments));
        }

        // check imported templates
        if (!found) {
            for (PebbleTemplateImpl template : context.getImportedTemplates()) {
                if (template.hasMacro(macroName)) {
                    found = true;
                    result = template.macro(context, macroName, args, false);
                }
            }
        }

        // delegate to parent template
        if (!found) {
            if (context.getHierarchy().getParent() != null) {
                PebbleTemplateImpl parent = context.getHierarchy().getParent();
                context.getHierarchy().ascend();
                result = parent.macro(context, macroName, args, true);
                context.getHierarchy().descend();
            } else {
                throw new PebbleException(null, String.format("Function or Macro [%s] does not exist.", macroName));
            }
        }

        return result;
    }

    public void setParent(EvaluationContext context, String parentName) throws PebbleException {
        context.getHierarchy()
                .pushAncestor((PebbleTemplateImpl) engine.getTemplate(this.resolveRelativePath(parentName)));
    }

    /**
     * Returns the template name
     *
     * @return The name of the template
     */
    public String getName() {
        return name;
    }

}
