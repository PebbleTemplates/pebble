package io.pebbletemplates.boot.autoconfigure;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.attributes.methodaccess.MethodAccessValidator;
import io.pebbletemplates.pebble.extension.Extension;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.pebble.node.ForNode;
import io.pebbletemplates.pebble.node.expression.AddExpression;
import io.pebbletemplates.pebble.node.expression.AndExpression;
import io.pebbletemplates.pebble.node.expression.ArrayExpression;
import io.pebbletemplates.pebble.node.expression.BinaryExpression;
import io.pebbletemplates.pebble.node.expression.BlockFunctionExpression;
import io.pebbletemplates.pebble.node.expression.ConcatenateExpression;
import io.pebbletemplates.pebble.node.expression.ContainsExpression;
import io.pebbletemplates.pebble.node.expression.ContextVariableExpression;
import io.pebbletemplates.pebble.node.expression.DivideExpression;
import io.pebbletemplates.pebble.node.expression.EqualsExpression;
import io.pebbletemplates.pebble.node.expression.FilterExpression;
import io.pebbletemplates.pebble.node.expression.FilterInvocationExpression;
import io.pebbletemplates.pebble.node.expression.FunctionOrMacroInvocationExpression;
import io.pebbletemplates.pebble.node.expression.GetAttributeExpression;
import io.pebbletemplates.pebble.node.expression.GreaterThanEqualsExpression;
import io.pebbletemplates.pebble.node.expression.GreaterThanExpression;
import io.pebbletemplates.pebble.node.expression.LessThanEqualsExpression;
import io.pebbletemplates.pebble.node.expression.LessThanExpression;
import io.pebbletemplates.pebble.node.expression.LiteralBigDecimalExpression;
import io.pebbletemplates.pebble.node.expression.LiteralBooleanExpression;
import io.pebbletemplates.pebble.node.expression.LiteralDoubleExpression;
import io.pebbletemplates.pebble.node.expression.LiteralIntegerExpression;
import io.pebbletemplates.pebble.node.expression.LiteralLongExpression;
import io.pebbletemplates.pebble.node.expression.LiteralNullExpression;
import io.pebbletemplates.pebble.node.expression.LiteralStringExpression;
import io.pebbletemplates.pebble.node.expression.MapExpression;
import io.pebbletemplates.pebble.node.expression.ModulusExpression;
import io.pebbletemplates.pebble.node.expression.MultiplyExpression;
import io.pebbletemplates.pebble.node.expression.NegativeTestExpression;
import io.pebbletemplates.pebble.node.expression.NotEqualsExpression;
import io.pebbletemplates.pebble.node.expression.OrExpression;
import io.pebbletemplates.pebble.node.expression.ParentFunctionExpression;
import io.pebbletemplates.pebble.node.expression.PositiveTestExpression;
import io.pebbletemplates.pebble.node.expression.RangeExpression;
import io.pebbletemplates.pebble.node.expression.RenderableNodeExpression;
import io.pebbletemplates.pebble.node.expression.SubtractExpression;
import io.pebbletemplates.pebble.node.expression.UnaryExpression;
import io.pebbletemplates.pebble.node.expression.UnaryMinusExpression;
import io.pebbletemplates.pebble.node.expression.UnaryNotExpression;
import io.pebbletemplates.pebble.node.expression.UnaryPlusExpression;
import io.pebbletemplates.spring.extension.SpringExtension;
import java.util.List;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;

@AutoConfiguration
@ConditionalOnClass(PebbleEngine.class)
@EnableConfigurationProperties(PebbleProperties.class)
@Import({PebbleServletWebConfiguration.class, PebbleReactiveWebConfiguration.class})
public class PebbleAutoConfiguration extends AbstractPebbleConfiguration {

  @Bean
  @ConditionalOnMissingBean(name = "pebbleLoader")
  public Loader<?> pebbleLoader(PebbleProperties properties) {
    ClasspathLoader loader = new ClasspathLoader();
    loader.setCharset(properties.getCharsetName());
    // classpath loader does not like leading slashes in resource paths
    loader.setPrefix(this.stripLeadingSlash(properties.getPrefix()));
    loader.setSuffix(properties.getSuffix());
    return loader;
  }

  @Bean
  @ConditionalOnMissingBean
  public SpringExtension springExtension(MessageSource messageSource) {
    return new SpringExtension(messageSource);
  }

  @Bean
  @ConditionalOnMissingBean(name = "pebbleEngine")
  public PebbleEngine pebbleEngine(PebbleProperties properties,
      Loader<?> pebbleLoader,
      SpringExtension springExtension,
      @Nullable List<Extension> extensions,
      @Nullable MethodAccessValidator methodAccessValidator) {
    PebbleEngine.Builder builder = new PebbleEngine.Builder();
    builder.loader(pebbleLoader);
    builder.extension(springExtension);
    if (extensions != null && !extensions.isEmpty()) {
      builder.extension(extensions.toArray(new Extension[extensions.size()]));
    }
    if (!properties.isCache()) {
      builder.cacheActive(false);
    }
    if (properties.getDefaultLocale() != null) {
      builder.defaultLocale(properties.getDefaultLocale());
    }
    builder.strictVariables(properties.isStrictVariables());
    builder.greedyMatchMethod(properties.isGreedyMatchMethod());
    if (methodAccessValidator != null) {
      builder.methodAccessValidator(methodAccessValidator);
    }
    return builder.build();
  }
}

class PebbleTemplatesHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    hints.reflection()
        .registerType(TypeReference.of(UnaryPlusExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(UnaryNotExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(UnaryMinusExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(SubtractExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(RenderableNodeExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(RangeExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(PositiveTestExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(ParentFunctionExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(OrExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(NotEqualsExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(NegativeTestExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(MultiplyExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(ModulusExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(MapExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(LiteralStringExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(LiteralNullExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(LiteralLongExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(LiteralIntegerExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(LiteralDoubleExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(LiteralBooleanExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(LessThanExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(LessThanEqualsExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(GreaterThanExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(GreaterThanEqualsExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(GetAttributeExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(FunctionOrMacroInvocationExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(FilterInvocationExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(FilterExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(EqualsExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(DivideExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(ContextVariableExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(ContainsExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(ConcatenateExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(BlockFunctionExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(BinaryExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(ArrayExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(AndExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(AddExpression.class),
            hint -> hint.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))
        .registerType(TypeReference.of(ForNode.class),
            hint -> hint.withMembers(MemberCategory.DECLARED_FIELDS,
                MemberCategory.DECLARED_CLASSES, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS));
  }
}
