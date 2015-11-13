/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.template;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.node.ArgumentsNode;
import com.mitchellbosecke.pebble.node.RootNode;
import com.mitchellbosecke.pebble.utils.FutureWriter;

public class PebbleTemplateImpl implements PebbleTemplate {

    /**
     * A template has to store a reference to the main engine so that it can
     * compile other templates when using the "import" or "include" tags. It's
     * important that the only method of the PebbleEngine that a template
     * invokes during evaluation is the "getTemplate" method because this is the
     * only one that I'm sure is thread-safe.
     */
    protected final PebbleEngine engine;

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

    public PebbleTemplateImpl(PebbleEngine engine, RootNode root, String name) throws PebbleException {
        this.engine = engine;
        this.rootNode = root;
        this.name = name;
    }

    public void buildContent(Writer writer, EvaluationContext context) throws IOException, PebbleException {
        rootNode.render(this, writer, context);
        if (context.getParentTemplate() != null) {
            PebbleTemplateImpl parent = context.getParentTemplate();
            context.ascendInheritanceChain();
            parent.buildContent(writer, context);
        }
    }

    public void evaluate(Writer writer) throws PebbleException, IOException {
        EvaluationContext context = initContext(null, null);
        evaluate(writer, context);
    }

    public void evaluate(Writer writer, Locale locale) throws PebbleException, IOException {
        EvaluationContext context = initContext(null, locale);
        evaluate(writer, context);
    }

    public void evaluate(Writer writer, Map<String, Object> map) throws PebbleException, IOException {
        EvaluationContext context = initContext(map, null);
        context.pushScope(map);
        evaluate(writer, context);
    }

    public void evaluate(Writer writer, Map<String, Object> map, Locale locale) throws PebbleException, IOException {
        EvaluationContext context = initContext(map, locale);
        context.pushScope(map);
        evaluate(writer, context);
    }

    /**
     * This is the authoritative evaluate method. It should not be invoked by
     * the end user and is therefore not included in the PebbleTemplate
     * interface. I can't, however, make it "private" due to the fact that
     * NodeInclude will call this method on a template other than itself.
     *
     *
     * @param writer
     *            The writer used to write the final output of the template
     * @param context
     *            The evaluation context
     * @throws PebbleException
     *             Thrown if any sort of template error occurs
     * @throws IOException
     *             Thrown from the writer object
     */
    public void evaluate(Writer writer, EvaluationContext context) throws PebbleException, IOException {
        if (context.getExecutorService() != null) {
            writer = new FutureWriter(writer);
        }
        buildContent(writer, context);
        writer.flush();
    }

    /**
     * Initializes the evaluation context with settings from the engine.
     *
     * @param locale
     *            The desired locale
     * @return The evaluation context
     */
    private EvaluationContext initContext(Map<String, Object> map, Locale locale) {
        locale = locale == null ? engine.getDefaultLocale() : locale;
        ScopeChain scopeChain = new ScopeChain(engine.getGlobalVariables());
        EvaluationContext context = new EvaluationContext(this, engine.isStrictVariables(), locale,
                engine.getFilters(), engine.getTests(), engine.getFunctions(), engine.getExecutorService(), scopeChain,
                null);
        return context;
    }

    /**
     * Imports a template.
     *
     * @param context
     *            The evaluation context
     * @param name
     *            The template name
     * @throws PebbleException
     *             Thrown if an error occurs while rendering the imported
     *             template
     */
    public void importTemplate(EvaluationContext context, String name) throws PebbleException {
        context.addImportedTemplate((PebbleTemplateImpl) engine.getTemplate(this.resolveRelativePath(name)));
    }

    /**
     * Includes a template with {@code name} into this template.
     *
     * @param writer
     *            the writer to which the output should be written to.
     * @param context
     *            the context within which the template is rendered in.
     * @param name
     *            the name of the template to include.
     * @param additionalVariables
     *            the map with additional variables provided with the include
     *            tag to add within the include tag.
     * @throws PebbleException Any error occurring during the compilation of the template
     * @throws IOException Any error during the loading of the template
     */
    public void includeTemplate(Writer writer, EvaluationContext context, String name, Map<?, ?> additionalVariables)
            throws PebbleException, IOException {
        PebbleTemplateImpl template = (PebbleTemplateImpl) engine.getTemplate(this.resolveRelativePath(name));
        EvaluationContext newContext = context.shallowCopyWithoutInheritanceChain(template);
        newContext.pushScope();
        for (Entry<?, ?> entry : additionalVariables.entrySet()) {
            newContext.put((String) entry.getKey(), entry.getValue());
        }
        template.evaluate(writer, newContext);
        newContext.popScope();
    }

    public boolean hasMacro(String macroName) {
        return macros.containsKey(macroName);
    }

    /**
     * This method resolves the given relative path based on this template file
     * path.
     *
     * @param relativePath
     *            the path which should be resolved.
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
     * @param block
     *            The block
     */
    public void registerBlock(Block block) {
        blocks.put(block.getName(), block);
    }

    public boolean hasBlock(String blockName) {
        return blocks.containsKey(blockName);
    }

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
     * @param blockName
     *            The name of the block
     * @param context
     *            The evaluation context
     * @param ignoreOverriden
     *            Whether or not to ignore overriden blocks
     * @param writer
     *            The writer
     * @throws PebbleException
     *             Thrown if an error occurs
     * @throws IOException
     *             Thrown from the writer object
     */
    public void block(Writer writer, EvaluationContext context, String blockName, boolean ignoreOverriden)
            throws PebbleException, IOException {

        PebbleTemplateImpl childTemplate = context.getChildTemplate();

        // check child
        if (!ignoreOverriden && childTemplate != null) {
            context.descendInheritanceChain();
            childTemplate.block(writer, context, blockName, false);
            context.ascendInheritanceChain();

            // check this template
        } else if (blocks.containsKey(blockName)) {
            Block block = blocks.get(blockName);
            block.evaluate(this, writer, context);

            // delegate to parent
        } else {
            if (context.getParentTemplate() != null) {
                PebbleTemplateImpl parent = context.getParentTemplate();
                context.ascendInheritanceChain();
                parent.block(writer, context, blockName, true);
                context.descendInheritanceChain();
            }
        }

    }

    public String macro(EvaluationContext context, String macroName, ArgumentsNode args, boolean ignoreOverriden)
            throws PebbleException {
        String result = null;
        boolean found = false;

        PebbleTemplateImpl childTemplate = context.getChildTemplate();

        // check child template first
        if (!ignoreOverriden && childTemplate != null) {
            found = true;
            context.descendInheritanceChain();
            result = childTemplate.macro(context, macroName, args, false);
            context.ascendInheritanceChain();

            // check current template
        } else if (hasMacro(macroName)) {
            found = true;
            Macro macro = macros.get(macroName);

            Map<String, Object> namedArguments = args.getArgumentMap(this, context, macro);
            result = macro.call(this, context, namedArguments);
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
            if (context.getParentTemplate() != null) {
                PebbleTemplateImpl parent = context.getParentTemplate();
                context.ascendInheritanceChain();
                result = parent.macro(context, macroName, args, true);
                context.descendInheritanceChain();
            } else {
                throw new PebbleException(null, String.format("Function or Macro [%s] does not exist.", macroName));
            }
        }

        return result;
    }

    public void setParent(EvaluationContext context, String parentName) throws PebbleException {
        context.setParent((PebbleTemplateImpl) engine.getTemplate(this.resolveRelativePath(parentName)));
    }

    public String getName() {
        return name;
    }

}
