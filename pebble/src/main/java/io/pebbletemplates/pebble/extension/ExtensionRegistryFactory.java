package io.pebbletemplates.pebble.extension;

import io.pebbletemplates.pebble.extension.core.AttributeResolverExtension;
import io.pebbletemplates.pebble.extension.i18n.I18nExtension;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.extension.core.CoreExtension;
import io.pebbletemplates.pebble.extension.escaper.EscaperExtension;
import io.pebbletemplates.pebble.extension.escaper.EscapingStrategy;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Provides configuration methods and builds the {@link ExtensionRegistry}. Used only internally by
 * the {@link PebbleEngine.Builder}.
 *
 */
public class ExtensionRegistryFactory {

  private final List<Extension> userProvidedExtensions = new ArrayList<>();

  private final EscaperExtension escaperExtension = new EscaperExtension();

  private boolean allowOverrideCoreOperators = false;

  private Function<Extension, Extension> customizer = Function.identity();

  public ExtensionRegistry buildExtensionRegistry() {
    ExtensionRegistry extensionRegistry = new ExtensionRegistry();

    Stream.of(new CoreExtension(), this.escaperExtension, new I18nExtension())
            .map(customizer::apply)
            .forEach(extensionRegistry::addExtension);

    for (Extension userProvidedExtension : this.userProvidedExtensions) {
      if (this.allowOverrideCoreOperators) {
        extensionRegistry.addOperatorOverridingExtension(userProvidedExtension);
      } else {
        extensionRegistry.addExtension(userProvidedExtension);
      }
    }

    extensionRegistry.addExtension(customizer.apply(new AttributeResolverExtension()));

    return extensionRegistry;
  }

  public void autoEscaping(boolean autoEscaping) {
    this.escaperExtension.setAutoEscaping(autoEscaping);
  }

  public void addEscapingStrategy(String name, EscapingStrategy strategy) {
    this.escaperExtension.addEscapingStrategy(name, strategy);
  }

  public void extension(Extension... extensions) {
    Collections.addAll(this.userProvidedExtensions, extensions);
  }

  public void allowOverrideCoreOperators(boolean allowOverrideCoreOperators) {
    this.allowOverrideCoreOperators = allowOverrideCoreOperators;
  }

  public void defaultEscapingStrategy(String strategy) {
    this.escaperExtension.setDefaultStrategy(strategy);
  }

  public void registerExtensionCustomizer(Function<Extension, ExtensionCustomizer> customizer) {
    this.customizer = customizer::apply;
  }

}