package com.mitchellbosecke.pebble.extension;

import com.mitchellbosecke.pebble.extension.core.AttributeResolverExtension;
import com.mitchellbosecke.pebble.extension.core.CoreExtension;
import com.mitchellbosecke.pebble.extension.escaper.EscaperExtension;
import com.mitchellbosecke.pebble.extension.escaper.EscapingStrategy;
import com.mitchellbosecke.pebble.extension.i18n.I18nExtension;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class ExtensionRegistryFactory {

  private final List<Extension> userProvidedExtensions = new ArrayList<>();

  private final EscaperExtension escaperExtension = new EscaperExtension();

  private boolean allowOverrideCoreOperators = false;

  private Map<Class<? extends Extension>, Function<Extension, Extension>> customizers = new HashMap<>();

  public ExtensionRegistry buildExtensionRegistry() {
    ExtensionRegistry extensionRegistry = new ExtensionRegistry();

    Stream.of(new CoreExtension(), this.escaperExtension, new I18nExtension())
            .map(this::applyCustomizer)
            .forEach(extensionRegistry::addExtension);

    for (Extension userProvidedExtension : this.userProvidedExtensions) {
      if (this.allowOverrideCoreOperators) {
        extensionRegistry.addOperatorOverridingExtension(userProvidedExtension);
      } else {
        extensionRegistry.addExtension(userProvidedExtension);
      }
    }

    extensionRegistry.addExtension(new AttributeResolverExtension());

    return extensionRegistry;
  }

  private Extension applyCustomizer(Extension coreExtension) {
    return customizers.getOrDefault(coreExtension.getClass(), Function.identity())
            .apply(coreExtension);
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

  public <T extends Extension> void addExtensionCustomizer(Class<T> clazz, Function<Extension, Extension> customizer) {
    this.customizers.put(clazz, customizer);
  }

}