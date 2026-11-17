package io.pebbletemplates.pebble.extension.writer;

import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SpecializedWriterTest {

  @Test
  void dispatchesValuesToSpecializedStringWriterMethods() {
    StringWriter output = new StringWriter();
    SpecializedWriter writer = new StringWriterSpecializedAdapter(output);

    writer.write("text");
    writer.write(12);
    writer.write(13L);
    writer.write(1.5d);
    writer.write(2.5f);
    writer.write((short) 6);
    writer.write((byte) 7);
    writer.write('c');
    writer.write(new BigDecimal("8.00"));
    writer.write(new Object() {
      @Override
      public String toString() {
        return "object";
      }
    });

    assertEquals("text12131.52.567c8.00object", output.toString());
    assertThrows(IllegalArgumentException.class, () -> writer.write(null));
  }

  @Test
  void pooledWriterAppendsCharactersAndResetsBetweenUses() {
    PooledSpecializedStringWriter writer = PooledSpecializedStringWriter.pooled();
    writer.write(new char[]{'x', 'y', 'z'}, 1, 2);
    writer.writeSpecialized("a");
    writer.writeSpecialized(1);
    writer.writeSpecialized(2L);
    writer.writeSpecialized(3.0d);
    writer.writeSpecialized(4.0f);
    writer.writeSpecialized((short) 5);
    writer.writeSpecialized((byte) 6);
    writer.writeSpecialized('7');
    writer.flush();
    writer.close();
    assertEquals("yza123.04.0567", writer.toString());

    assertEquals("", PooledSpecializedStringWriter.pooled().toString());
  }
}
