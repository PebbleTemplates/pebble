package io.pebbletemplates.pebble.attributes.methodaccess;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class NoOpMethodAccessValidatorTest {

  private final MethodAccessValidator underTest = NoOpMethodAccessValidator.builder().build();

  @Test
  void whenIsMethodAccessAllowed_thenReturnTrue() {
    boolean methodAccessAllowed = this.underTest.isMethodAccessAllowed(null, null);

    assertThat(methodAccessAllowed).isTrue();
  }
}
