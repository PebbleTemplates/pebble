package io.pebbletemplates.pebble.extension.core;

import io.pebbletemplates.pebble.extension.Extension;
import io.pebbletemplates.pebble.extension.ExtensionCustomizer;
import io.pebbletemplates.pebble.extension.Filter;
import io.pebbletemplates.pebble.extension.Test;
import io.pebbletemplates.pebble.operator.BinaryOperator;
import io.pebbletemplates.pebble.operator.UnaryOperator;
import io.pebbletemplates.pebble.tokenParser.TokenParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author freshchen
 * @since 2024/3/2
 */
public class DisallowExtensionCustomizerBuilder {

  private Collection<String> disallowedFilterKeys;

  private Collection<String> disallowedTokenParserTags;

  private Collection<String> disallowedFunctionKeys;

  private Collection<String> disallowedBinaryOperatorSymbols;

  private Collection<String> disallowedUnaryOperatorSymbols;

  private Collection<String> disallowedTestKeys;


  public static DisallowExtensionCustomizerBuilder builder() {
    return new DisallowExtensionCustomizerBuilder();
  }

  public DisallowExtensionCustomizerBuilder disallowedFunctionKeys(Collection<String> disallowedFunctionKeys) {
    this.disallowedFunctionKeys = disallowedFunctionKeys;
    return this;
  }

  public DisallowExtensionCustomizerBuilder disallowedTokenParserTags(Collection<String> disallowedTokenParserTags) {
    this.disallowedTokenParserTags = disallowedTokenParserTags;
    return this;
  }

  public DisallowExtensionCustomizerBuilder disallowedFilterKeys(Collection<String> disallowedFilterKeys) {
    this.disallowedFilterKeys = disallowedFilterKeys;
    return this;
  }

  public DisallowExtensionCustomizerBuilder disallowedUnaryOperatorSymbols(Collection<String> disallowedUnaryOperatorSymbols) {
    this.disallowedUnaryOperatorSymbols = disallowedUnaryOperatorSymbols;
    return this;
  }

  public DisallowExtensionCustomizerBuilder disallowedBinaryOperatorSymbols(Collection<String> disallowedBinaryOperatorSymbols) {
    this.disallowedBinaryOperatorSymbols = disallowedBinaryOperatorSymbols;
    return this;
  }

  public DisallowExtensionCustomizerBuilder disallowedTestKeys(Collection<String> disallowedTestKeys) {
    this.disallowedTestKeys = disallowedTestKeys;
    return this;
  }

  public Function<Extension, ExtensionCustomizer> build() {

    return extension -> new ExtensionCustomizer(extension) {

      @Override
      public Map<String, Test> getTests() {
        return disallow(super::getTests, disallowedTestKeys);
      }

      @Override
      public List<UnaryOperator> getUnaryOperators() {
        return disallow(super::getUnaryOperators, disallowedUnaryOperatorSymbols, UnaryOperator::getSymbol);
      }

      @Override
      public List<BinaryOperator> getBinaryOperators() {
        return disallow(super::getBinaryOperators, disallowedBinaryOperatorSymbols, BinaryOperator::getSymbol);
      }

      @Override
      public Map<String, io.pebbletemplates.pebble.extension.Function> getFunctions() {
        return disallow(super::getFunctions, disallowedFunctionKeys);
      }

      @Override
      public Map<String, Filter> getFilters() {
        return disallow(super::getFilters, disallowedFilterKeys);
      }

      @Override
      public List<TokenParser> getTokenParsers() {
        return disallow(super::getTokenParsers, disallowedTokenParserTags, TokenParser::getTag);
      }

      private <T> List<T> disallow(Supplier<List<T>> superGetter,
                                   Collection<String> disallowedList,
                                   Function<T, String> keyGetter) {
        List<T> superList = superGetter.get();
        if (disallowedList == null || disallowedList.isEmpty()) {
          return superList;
        }

        List<T> result = Optional.ofNullable(superList).map(ArrayList::new)
                .orElseGet(ArrayList::new);

        disallowedList.stream()
                .filter(Objects::nonNull)
                .forEach(v -> result.removeIf(t -> v.equals(keyGetter.apply(t))));

        return result;
      }

      private <T> Map<String, T> disallow(Supplier<Map<String, T>> superGetter,
                                          Collection<String> disallowedList) {
        Map<String, T> superMap = superGetter.get();
        if (disallowedList == null || disallowedList.isEmpty()) {
          return superMap;
        }

        Map<String, T> result = Optional.ofNullable(superMap).map(HashMap::new)
                .orElseGet(HashMap::new);

        disallowedList.stream()
                .filter(Objects::nonNull)
                .forEach(result::remove);

        return result;
      }

    };
  }


}
