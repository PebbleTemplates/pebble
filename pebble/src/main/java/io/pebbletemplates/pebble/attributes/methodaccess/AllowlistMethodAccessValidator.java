package io.pebbletemplates.pebble.attributes.methodaccess;

import io.pebbletemplates.pebble.node.ForNode;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * A {@link MethodAccessValidator} that denies every method call unless it has been explicitly
 * allowed.
 *
 * <p>Rules are configured through {@link #builder()}:
 * <ul>
 *   <li>{@link Builder#allowTypes(Class[])}: methods exposed by the given type can be called on any
 *   instance of that type (including subtypes). Methods only declared by a subtype are not
 *   allowed.</li>
 *   <li>{@link Builder#allowPackages(String...)}: every public method can be called on instances
 *   of classes in the given packages or their sub-packages.</li>
 *   <li>{@link Builder#allowMethod(Class, String...)}: only the given methods, exposed by the given
 *   type, can be called on instances of that type.</li>
 *   <li>{@link Builder#allowDefaults()}: allows common, side-effect free JDK types (strings, boxed
 *   primitives, collections, maps, {@link Locale}, {@code java.time}, ...).</li>
 * </ul>
 *
 * <p>The objects Pebble itself exposes to templates ({@code loop} variables and
 * {@code template.name}) are always allowed.
 *
 * <p>Regardless of the configured rules, the following are always denied: static methods, the
 * {@code getClass}, {@code wait}, {@code notify} and {@code notifyAll} methods, and any method
 * called on a {@link Class}, {@link ClassLoader}, reflection object, {@link Runtime},
 * {@link Thread} or {@link ThreadGroup}.
 *
 * <p>Instances are immutable and thread-safe.
 */
public final class AllowlistMethodAccessValidator implements MethodAccessValidator {

  private static final Set<String> FORBIDDEN_METHODS = Collections.unmodifiableSet(
      new HashSet<>(Arrays.asList("getClass", "wait", "notify", "notifyAll")));

  private static final List<Class<?>> FORBIDDEN_TYPES = Collections.unmodifiableList(
      Arrays.asList(Class.class, ClassLoader.class, AccessibleObject.class, Runtime.class,
          Thread.class, ThreadGroup.class));

  private static final List<Class<?>> DEFAULT_TYPES = Collections.unmodifiableList(Arrays.asList(
      String.class,
      CharSequence.class,
      Boolean.class,
      Character.class,
      Number.class,
      Byte.class,
      Short.class,
      Integer.class,
      Long.class,
      Float.class,
      Double.class,
      BigInteger.class,
      BigDecimal.class,
      Enum.class,
      Comparable.class,
      Iterable.class,
      Collection.class,
      List.class,
      Set.class,
      Map.class,
      Map.Entry.class,
      Optional.class,
      UUID.class,
      Date.class,
      Locale.class));

  private static final List<String> DEFAULT_PACKAGES = Collections.singletonList("java.time");

  // objects exposed by Pebble itself to every template (loop variables, template global)
  private static final Map<Class<?>, Set<String>> BUILTIN_METHODS = createBuiltinMethods();

  private final List<Class<?>> allowedTypes;
  private final List<String> allowedPackages;
  private final Map<Class<?>, Set<String>> allowedMethods;

  private AllowlistMethodAccessValidator(Builder builder) {
    this.allowedTypes = Collections.unmodifiableList(new ArrayList<>(builder.allowedTypes));
    this.allowedPackages = Collections.unmodifiableList(new ArrayList<>(builder.allowedPackages));
    Map<Class<?>, Set<String>> methods = new HashMap<>();
    for (Map.Entry<Class<?>, Set<String>> entry : builder.allowedMethods.entrySet()) {
      methods.put(entry.getKey(), Collections.unmodifiableSet(new HashSet<>(entry.getValue())));
    }
    this.allowedMethods = Collections.unmodifiableMap(methods);
  }

  /**
   * Creates a new builder. Without any rule, the resulting validator denies every method call.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  public boolean isMethodAccessAllowed(Object object, Method method) {
    if (object == null || method == null || this.isAlwaysForbidden(object, method)) {
      return false;
    }
    return isAllowedByMethod(BUILTIN_METHODS, object, method)
        || this.isAllowedByType(object, method)
        || this.isAllowedByPackage(object)
        || isAllowedByMethod(this.allowedMethods, object, method);
  }

  private static Map<Class<?>, Set<String>> createBuiltinMethods() {
    Map<Class<?>, Set<String>> methods = new HashMap<>();
    methods.put(ForNode.LoopVariables.class, Collections.unmodifiableSet(new HashSet<>(
        Arrays.asList("isFirst", "isLast", "getLength", "getIndex", "getRevindex"))));
    methods.put(PebbleTemplate.class, Collections.singleton("getName"));
    return Collections.unmodifiableMap(methods);
  }

  private boolean isAlwaysForbidden(Object object, Method method) {
    if (Modifier.isStatic(method.getModifiers()) || FORBIDDEN_METHODS.contains(method.getName())) {
      return true;
    }
    for (Class<?> forbiddenType : FORBIDDEN_TYPES) {
      if (forbiddenType.isInstance(object)) {
        return true;
      }
    }
    return false;
  }

  private boolean isAllowedByType(Object object, Method method) {
    for (Class<?> type : this.allowedTypes) {
      if (type.isInstance(object) && exposesMethod(type, method)) {
        return true;
      }
    }
    return false;
  }

  private boolean isAllowedByPackage(Object object) {
    String className = object.getClass().getName();
    for (String allowedPackage : this.allowedPackages) {
      if (className.startsWith(allowedPackage + ".")) {
        return true;
      }
    }
    return false;
  }

  private static boolean isAllowedByMethod(Map<Class<?>, Set<String>> allowedMethods,
      Object object, Method method) {
    for (Map.Entry<Class<?>, Set<String>> entry : allowedMethods.entrySet()) {
      Class<?> type = entry.getKey();
      if (type.isInstance(object) && entry.getValue().contains(method.getName())
          && exposesMethod(type, method)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks that the given type exposes a public method with the same signature, so that methods
   * only declared by a subtype are not allowed through a supertype rule.
   */
  private static boolean exposesMethod(Class<?> type, Method method) {
    if (findPublicMethod(type, method) != null) {
      return true;
    }
    // interfaces do not expose the public methods of Object (toString, equals, hashCode)
    return type.isInterface() && findPublicMethod(Object.class, method) != null;
  }

  private static Method findPublicMethod(Class<?> type, Method method) {
    try {
      return type.getMethod(method.getName(), method.getParameterTypes());
    } catch (NoSuchMethodException | SecurityException e) {
      return null;
    }
  }

  /**
   * Builder for {@link AllowlistMethodAccessValidator}.
   */
  public static final class Builder {

    private final Set<Class<?>> allowedTypes = new LinkedHashSet<>();
    private final Set<String> allowedPackages = new LinkedHashSet<>();
    private final Map<Class<?>, Set<String>> allowedMethods = new HashMap<>();

    private Builder() {
    }

    /**
     * Allows common, side-effect free JDK types: {@link String}, {@link CharSequence}, boxed
     * primitives, {@link Number}, {@link BigInteger}, {@link BigDecimal}, {@link Enum},
     * {@link Comparable}, {@link Iterable}, {@link Collection}, {@link List}, {@link Set},
     * {@link Map}, {@link Map.Entry}, {@link Optional}, {@link UUID}, {@link Date},
     * {@link Locale} and the
     * {@code java.time} package.
     *
     * @return this builder
     */
    public Builder allowDefaults() {
      this.allowedTypes.addAll(DEFAULT_TYPES);
      this.allowedPackages.addAll(DEFAULT_PACKAGES);
      return this;
    }

    /**
     * Allows calling the public methods exposed by the given types on any instance of these types.
     *
     * @param types the types to allow
     * @return this builder
     */
    public Builder allowTypes(Class<?>... types) {
      for (Class<?> type : requireNonNullElements(types, "types")) {
        this.allowedTypes.add(type);
      }
      return this;
    }

    /**
     * Allows calling every public method on instances of classes in the given packages or their
     * sub-packages.
     *
     * @param packageNames the package names, e.g. {@code com.example.model}
     * @return this builder
     */
    public Builder allowPackages(String... packageNames) {
      for (String packageName : requireNonNullElements(packageNames, "packageNames")) {
        String trimmed = packageName.trim();
        while (trimmed.endsWith(".")) {
          trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        if (trimmed.isEmpty()) {
          throw new IllegalArgumentException("Package name must not be empty");
        }
        this.allowedPackages.add(trimmed);
      }
      return this;
    }

    /**
     * Allows calling the given methods, exposed by the given type, on any instance of that type.
     *
     * @param type the type exposing the methods
     * @param methodNames the method names to allow
     * @return this builder
     */
    public Builder allowMethod(Class<?> type, String... methodNames) {
      if (type == null) {
        throw new IllegalArgumentException("type must not be null");
      }
      Set<String> names = this.allowedMethods.get(type);
      if (names == null) {
        names = new HashSet<>();
        this.allowedMethods.put(type, names);
      }
      names.addAll(Arrays.asList(requireNonNullElements(methodNames, "methodNames")));
      return this;
    }

    /**
     * Creates the validator.
     *
     * @return a new immutable validator
     */
    public AllowlistMethodAccessValidator build() {
      return new AllowlistMethodAccessValidator(this);
    }

    private static <T> T[] requireNonNullElements(T[] values, String name) {
      if (values == null) {
        throw new IllegalArgumentException(name + " must not be null");
      }
      for (T value : values) {
        if (value == null) {
          throw new IllegalArgumentException(name + " must not contain null");
        }
      }
      return values;
    }
  }
}
