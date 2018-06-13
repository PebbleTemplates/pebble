package com.mitchellbosecke.pebble.extension.writer;

import java.io.Writer;

/**
 * A ${@link SpecializedWriter} that's pooled in a ${@link ThreadLocal}. It's backed by a ${@link
 * StringBuilder} so it's not threadsafe but doesn't involve synchronization. Beware that it has
 * some limitations:
 * <ul>
 * <li>As it's backed by a ${@link ThreadLocal}, it might leak in environments where ClassLoaders
 * are rebooted at runtime</li>
 * <li>It doesn't take any security measure against very large payloads that would cause underlying
 * buffers to eat memory</li>
 * </ul>
 */
public class PooledSpecializedStringWriter extends Writer implements SpecializedWriter {

  private static final ThreadLocal<PooledSpecializedStringWriter> POOL = ThreadLocal
      .withInitial(PooledSpecializedStringWriter::new);

  private StringBuilder sb = new StringBuilder();

  private PooledSpecializedStringWriter() {
  }

  @Override
  public void writeSpecialized(int i) {
    sb.append(i);
  }

  @Override
  public void writeSpecialized(long l) {
    sb.append(l);
  }

  @Override
  public void writeSpecialized(double d) {
    sb.append(d);
  }

  @Override
  public void writeSpecialized(float f) {
    sb.append(f);
  }

  @Override
  public void writeSpecialized(short s) {
    sb.append(s);
  }

  @Override
  public void writeSpecialized(byte b) {
    sb.append(b);
  }

  @Override
  public void writeSpecialized(char c) {
    sb.append(c);
  }

  @Override
  public void writeSpecialized(String s) {
    sb.append(s);
  }

  @Override
  public void write(char[] cbuf, int off, int len) {
    sb.append(cbuf, off, len);
  }

  @Override
  public void flush() {
  }

  @Override
  public void close() {
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  public static PooledSpecializedStringWriter pooled() {
    PooledSpecializedStringWriter pooled = POOL.get();
    pooled.sb.setLength(0);
    return pooled;
  }
}
