package io.pebbletemplates.pebble.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class OperatorUtilsToNumberTest {

    @ParameterizedTest(name = "Number input {0} should be returned as-is")
    @MethodSource("numberInputs")
    void shouldReturnNumberInstanceAsIs(Number input) {
        Number result = OperatorUtils.toNumber(input);
        assertThat(result).isSameAs(input);
    }

    static Stream<Number> numberInputs() {
        return Stream.of(42, 3.14f, 100L, 2.718281828, 2.5d, (short) 7);
    }

    @ParameterizedTest(name = "String {0} should parse to Double {1}")
    @MethodSource("decimalStringInputs")
    void shouldParseDecimalStringAsDouble(String input, double expected) {
        Number result = OperatorUtils.toNumber(input);
        assertThat(result)
            .isInstanceOf(Double.class)
            .isEqualTo(expected);
    }

    static Stream<Arguments> decimalStringInputs() {
        return Stream.of(
            arguments("3.14",   3.14),
            arguments("0.0",    0.0),
            arguments("-1.5",  -1.5),
            arguments("1e10",   1e10),
            arguments("1E10",   1E10),
            arguments("2.5e3",  2.5e3),
            arguments("1.1E-2", 1.1E-2)
        );
    }

    @ParameterizedTest(name = "String {0} should parse to Long {1}")
    @MethodSource("integerStringInputs")
    void shouldParseIntegerStringAsLong(String input, long expected) {
        Number result = OperatorUtils.toNumber(input);
        assertThat(result)
            .isInstanceOf(Long.class)
            .isEqualTo(expected);
    }

    static Stream<Arguments> integerStringInputs() {
        return Stream.of(
            arguments("0",                    0L),
            arguments("42",                  42L),
            arguments("-7",                  -7L),
            arguments("9999999999",  9999999999L)  // larger than Integer.MAX_VALUE
        );
    }

    @ParameterizedTest(name = "Unsupported type {0} should throw IllegalArgumentException")
    @MethodSource("unsupportedInputs")
    void shouldThrowForUnsupportedTypes(Object input) {
        assertThatThrownBy(() -> OperatorUtils.toNumber(input))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cannot convert to Number");
    }

    static Stream<Object> unsupportedInputs() {
        return Stream.of(null, true, 'x', new Object());
    }

    @ParameterizedTest(name = "Malformed string {0} should throw NumberFormatException")
    @ValueSource(strings = {"abc", "1.2.3", "1e", "--1", ""})
    void shouldThrowForMalformedStrings(String input) {
        assertThatThrownBy(() -> OperatorUtils.toNumber(input))
            .isInstanceOf(NumberFormatException.class);
    }
}