/*
 * This file is part of Pebble.
 * <p>
 * Copyright (c) 2014 by Mitchell Bösecke
 * <p>
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.pebble.template;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.escaper.SafeString;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.node.BlockNode;
import io.pebbletemplates.pebble.node.BodyNode;
import io.pebbletemplates.pebble.node.RenderableNode;
import io.pebbletemplates.pebble.node.RootNode;
import io.pebbletemplates.pebble.utils.FutureWriter;
import io.pebbletemplates.pebble.utils.LimitedSizeWriter;
import io.pebbletemplates.pebble.utils.Pair;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The actual implementation of a PebbleTemplate
 */
public class PebbleTemplateImpl implements PebbleTemplate {

  /**
   * A template has to store a reference to the main engine so that it can compile other templates
   * when using the "import" or "include" tags.
   * <p>
   * It will also retrieve some stateful information such as the default locale when necessary.
   * Luckily, the engine is immutable so this should be thread safe.
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
  private final RenderableNode rootNode;

  /**
   * Name of template. Used to help with debugging.
   */
  private final String name;

  /**
   * Constructor
   *
   * @param engine The pebble engine used to construct this template
   * @param root The root not to evaluate
   * @param name The name of the template
   */
  public PebbleTemplateImpl(PebbleEngine engine, RenderableNode root, String name) {
    this.engine = engine;
    this.rootNode = root;
    this.name = name;
  }

  public void evaluate(Writer writer) throws IOException {
    EvaluationContextImpl context = this.initContext(null);
    this.evaluate(writer, context);
  }

  public void evaluate(Writer writer, Locale locale) throws IOException {
    EvaluationContextImpl context = this.initContext(locale);
    this.evaluate(writer, context);
  }

  public void evaluate(Writer writer, Map<String, Object> map) throws IOException {
    EvaluationContextImpl context = this.initContext(null);
    context.getScopeChain().pushScope(map);

    // Issue #449: if the provided map is immutable, this allows us to still set variables in the template context
    context.getScopeChain().pushScope(new HashMap<>());
    this.evaluate(writer, context);
  }

  public void evaluate(Writer writer, Map<String, Object> map, Locale locale) throws IOException {
    EvaluationContextImpl context = this.initContext(locale);
    context.getScopeChain().pushScope(map);

    // Issue #449: if the provided map is immutable, this allows us to still set variables in the template context
    context.getScopeChain().pushScope(new HashMap<>());
    this.evaluate(writer, context);
  }

  public void evaluateBlock(String blockName, Writer writer) throws IOException {
    EvaluationContextImpl context = this.initContext(null);
    this.evaluate(new NoopWriter(), context);

    this.block(writer, context, blockName, false);
    writer.flush();
  }

  public void evaluateBlock(String blockName, Writer writer, Locale locale) throws IOException {
    EvaluationContextImpl context = this.initContext(locale);
    this.evaluate(new NoopWriter(), context);

    this.block(writer, context, blockName, false);
    writer.flush();
  }

  public void evaluateBlock(String blockName, Writer writer, Map<String, Object> map)
      throws IOException {
    EvaluationContextImpl context = this.initContext(null);
    context.getScopeChain().pushScope(map);
    this.evaluate(new NoopWriter(), context);

    this.block(writer, context, blockName, false);
    writer.flush();
  }

  public void evaluateBlock(String blockName, Writer writer, Map<String, Object> map, Locale locale)
      throws IOException {
    EvaluationContextImpl context = this.initContext(locale);
    context.getScopeChain().pushScope(map);
    this.evaluate(new NoopWriter(), context);

    this.block(writer, context, blockName, false);
    writer.flush();
  }

  /**
   * This is the authoritative evaluate method. It will evaluate the template starting at the root
   * node.
   *
   * @param writer The writer used to write the final output of the template
   * @param context The evaluation context
   * @throws IOException Thrown from the writer object
   */
  private void evaluate(Writer writer, EvaluationContextImpl context) throws IOException {
    if (context.getExecutorService() != null) {
      writer = new FutureWriter(writer);
    }
    writer = LimitedSizeWriter.from(writer, context);
    this.rootNode.render(this, writer, context);

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
  private EvaluationContextImpl initContext(Locale locale) {
    locale = locale == null ? this.engine.getDefaultLocale() : locale;

    // globals
    ScopeChain scopeChain = new ScopeChain();
    Map<String, Object> globals = new HashMap<>();
    globals.put("locale", locale);
    globals.put("template", this);
    globals.put("_context", new GlobalContext(scopeChain));

    scopeChain.pushScope(globals);

    // global vars provided from extensions
    scopeChain.pushScope(this.engine.getExtensionRegistry().getGlobalVariables());

    return new EvaluationContextImpl(this, this.engine.isStrictVariables(), locale, this.engine.getMaxRenderedSize(),
        this.engine.getExtensionRegistry(), this.engine.getTagCache(),
        this.engine.getExecutorService(),
        new ArrayList<>(), new HashMap<>(), scopeChain, null, this.engine.getEvaluationOptions());
  }

  /**
   * Return a shallow copy of this template.
   *
   * @return A new template instance with the same data
   */
  private PebbleTemplateImpl shallowCopy() {
    PebbleTemplateImpl copy = new PebbleTemplateImpl(engine, rootNode, name);
    copy.blocks.putAll(this.blocks);
    copy.macros.putAll(this.macros);

    return copy;
  }

  /**
   * Imports a template.
   *
   * @param context The evaluation context
   * @param name The template name
   */
  public void importTemplate(EvaluationContextImpl context, String name) {
    context.getImportedTemplates()
        .add((PebbleTemplateImpl) this.engine.getTemplate(this.resolveRelativePath(name)));
  }

  /**
   * Imports a named template.
   *
   * @param context The evaluation context
   * @param name The template name
   * @param alias The template alias
   */
  public void importNamedTemplate(EvaluationContextImpl context, String name, String alias) {
    context.addNamedImportedTemplates(alias,
        (PebbleTemplateImpl) this.engine.getTemplate(this.resolveRelativePath(name)));
  }

  /**
   * Imports named macros from specified template.
   *
   * @param name The template name
   * @param namedMacros named macros
   */
  public void importNamedMacrosFromTemplate(String name, List<Pair<String, String>> namedMacros) {
    PebbleTemplateImpl templateImpl = (PebbleTemplateImpl) this.engine
        .getTemplate(this.resolveRelativePath(name));
    for (Pair<String, String> pair : namedMacros) {
      Macro m = templateImpl.macros.get(pair.getRight());

      if (m == null) {
        throw new PebbleException(null, "Function or Macro [" + pair.getRight() + "] referenced by alias ["
                + pair.getLeft() + "] does not exist.");
      }

      this.registerMacro(pair.getLeft(), m);
    }
  }

  /**
   * Returns a named template.
   *
   * @param context The evaluation context
   * @param alias The template alias
   */
  public PebbleTemplateImpl getNamedImportedTemplate(EvaluationContextImpl context, String alias) {
    return context.getNamedImportedTemplate(alias);
  }

  /**
   * Includes a template with {@code name} into this template.
   *
   * @param writer the writer to which the output should be written to.
   * @param context the context within which the template is rendered in.
   * @param name the name of the template to include.
   * @param additionalVariables the map with additional variables provided with the include tag to
   * add within the include tag.
   * @throws IOException Any error during the loading of the template
   */
  public void includeTemplate(Writer writer, EvaluationContextImpl context, String name,
      Map<?, ?> additionalVariables) throws IOException {
    PebbleTemplateImpl template = (PebbleTemplateImpl) this.engine
        .getTemplate(this.resolveRelativePath(name));
    EvaluationContextImpl newContext = context.shallowCopyWithoutInheritanceChain(template);
    ScopeChain scopeChain = newContext.getScopeChain();
    scopeChain.pushScope();
    for (Entry<?, ?> entry : additionalVariables.entrySet()) {
      scopeChain.put((String) entry.getKey(), entry.getValue());
    }
    template.evaluate(writer, newContext);
    scopeChain.popScope();
  }

  /**
   * Embed a template with {@code name} into this template and override its child blocks. This has the effect of
   * essentially "including" a template (as with the `include` tag), but its blocks may be overridden in the calling
   * template similar to extending a template.
   *
   * @param lineNo the line number of the node being evaluated
   * @param writer the writer to which the output should be written to.
   * @param context the context within which the template is rendered in.
   * @param name the name of the template to include.
   * @param additionalVariables the map with additional variables provided with the include tag to
   * add within the embed tag.
   * @param overriddenBlocks the blocks parsed out of the parent template that should override blocks in the embedded template
   * @throws IOException Any error during the loading of the template
   */
  public void embedTemplate(
          int lineNo,
          Writer writer,
          EvaluationContextImpl context,
          String name,
          Map<?, ?> additionalVariables,
          List<BlockNode> overriddenBlocks
  ) throws IOException {
    // get the template to embed
    String embeddedTemplateName = this.resolveRelativePath(name);

    // make a shallow copy of the template so we can safely modify its blocks without affecting other templates in the
    // template cache. Include and extend will use the same object from the cache, so we need to make sure embeds do not
    // impact those other tags or change anything in the cache.
    final PebbleTemplateImpl embeddedTemplate =
            ((PebbleTemplateImpl) this.engine.getTemplate(embeddedTemplateName)).shallowCopy();

    // push a child scope based on the current scope
    context.scopedShallowWithoutInheritanceChain(embeddedTemplate, additionalVariables, (newContext) -> {

      // create a fake root template to act as the parent of the embedded template. That root node simply renders the
      // embedded template's own RootNode, but now we're able to isolate its template hierarchy and provide new blocks
      // into that hierarchy
      BodyNode embeddedTemplateBody = ((RootNode) embeddedTemplate.rootNode).getBody();
      BodyNode bodyNode = new BodyNode(lineNo, Collections.singletonList(embeddedTemplateBody));
      PebbleTemplateImpl fakeRootTemplate = new PebbleTemplateImpl(engine, bodyNode, embeddedTemplateName);

      // push the blocks from the embedded template into the fake root, to make sure they are able to rendered if they
      // are not overridden
      for(Block block : embeddedTemplate.blocks.values()) {
        fakeRootTemplate.registerBlock(block);
      }

      // push the overridden blocks into the embedded template, since they were added to the host template rather than
      // the embdedded template during parsing. Overridden blocks must be present in the embedded template.
      for(BlockNode blockNode : overriddenBlocks) {
        embeddedTemplate.registerBlock(blockNode.getBlock());
      }

      // push the new fake template root into the child context so blocks are resolved properly.
      newContext.getHierarchy().pushAncestor(fakeRootTemplate);

      // evaluate the embedded template. Its blocks will now override those defined in the fake root template using the
      // same mechanism as for overriding blocks when extending a template
      embeddedTemplate.evaluate(writer, newContext);
    });
  }

  /**
   * Checks if a macro exists
   *
   * @param macroName The name of the macro
   * @return Whether or not the macro exists
   */
  public boolean hasMacro(String macroName) {
    return this.macros.containsKey(macroName);
  }

  /**
   * Checks if a block exists
   *
   * @param blockName The name of the block
   * @return Whether or not the block exists
   */
  public boolean hasBlock(String blockName) {
    return this.blocks.containsKey(blockName);
  }

  /**
   * This method resolves the given relative path based on this template file path.
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
    this.blocks.put(block.getName(), block);
  }

  /**
   * Registers a macro
   *
   * @param macro The macro
   */
  public void registerMacro(Macro macro) {
    if (this.macros.containsKey(macro.getName())) {
      throw new PebbleException(null,
          "More than one macro can not share the same name: " + macro.getName());
    }
    this.macros.put(macro.getName(), macro);
  }

  /**
   * Registers a macro with alias
   *
   * @param macro The macro
   * @throws PebbleException Throws exception if macro already exists with the same name
   */
  public void registerMacro(String alias, Macro macro) {
    if (this.macros.containsKey(alias)) {
      throw new PebbleException(null, "More than one macro can not share the same name: " + alias);
    }
    this.macros.put(alias, macro);
  }

  /**
   * A typical block declaration will use this method which evaluates the block using the regular
   * user-provided writer.
   *
   * @param blockName The name of the block
   * @param context The evaluation context
   * @param ignoreOverriden Whether or not to ignore overriden blocks
   * @param writer The writer
   * @throws IOException Thrown from the writer object
   */
  public void block(Writer writer, EvaluationContextImpl context, String blockName,
      boolean ignoreOverriden) throws IOException {

    Hierarchy hierarchy = context.getHierarchy();
    PebbleTemplateImpl childTemplate = hierarchy.getChild();

    // check child
    if (!ignoreOverriden && childTemplate != null) {
      hierarchy.descend();
      childTemplate.block(writer, context, blockName, false);
      hierarchy.ascend();

      // check this template
    } else if (this.blocks.containsKey(blockName)) {
      Block block = this.blocks.get(blockName);
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
   * @param context The evaluation context
   * @param macroName The name of the macro
   * @param args The arguments
   * @param ignoreOverriden Whether or not to ignore macro definitions in child template
   * @return The results of the macro invocation
   */
  public SafeString macro(EvaluationContextImpl context, String macroName, ArgumentsNode args,
      boolean ignoreOverriden, int lineNumber) {
    SafeString result = null;
    boolean found = false;

    PebbleTemplateImpl childTemplate = context.getHierarchy().getChild();

    // check child template first
    if (!ignoreOverriden && childTemplate != null) {
      found = true;
      context.getHierarchy().descend();
      result = childTemplate.macro(context, macroName, args, false, lineNumber);
      context.getHierarchy().ascend();

      // check current template
    } else if (this.hasMacro(macroName)) {
      found = true;
      Macro macro = this.macros.get(macroName);

      Map<String, Object> namedArguments = args.getArgumentMap(this, context, macro);
      result = new SafeString(macro.call(this, context, namedArguments));
    }

    // check imported templates
    if (!found) {
      for (PebbleTemplateImpl template : context.getImportedTemplates()) {
        if (template.hasMacro(macroName)) {
          found = true;
          result = template.macro(context, macroName, args, false, lineNumber);
          // If a macro was found and executed, dont search for more
          break;
        }
      }
    }

    // delegate to parent template
    if (!found) {
      if (context.getHierarchy().getParent() != null) {
        PebbleTemplateImpl parent = context.getHierarchy().getParent();
        context.getHierarchy().ascend();
        result = parent.macro(context, macroName, args, true, lineNumber);
        context.getHierarchy().descend();
      } else {
        throw new PebbleException(null,
            String.format("Function or Macro [%s] does not exist.", macroName), lineNumber,
            this.name);
      }
    }

    return result;
  }

  public void setParent(EvaluationContextImpl context, String parentName) {
    context.getHierarchy()
        .pushAncestor(
            (PebbleTemplateImpl) this.engine.getTemplate(this.resolveRelativePath(parentName)));
  }

  /**
   * Returns the template name
   *
   * @return The name of the template
   */
  public String getName() {
    return this.name;
  }

  private static class NoopWriter extends Writer {

    public void write(char[] cbuf, int off, int len) {
    }

    public void flush() {
    }

    public void close() {
    }
  }
}
