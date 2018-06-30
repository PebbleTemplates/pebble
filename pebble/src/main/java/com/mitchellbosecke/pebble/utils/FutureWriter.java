/*
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.mitchellbosecke.pebble.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A Writer that will wrap around the user-provided writer if the user also provided an
 * ExecutorService to the main PebbleEngine. A FutureWriter is capable of handling Futures that will
 * return a string.
 *
 * It is not thread safe but that is okay. Each thread will have it's own writer, provided by the
 * "parallel" node; i.e. they will never share writers.
 *
 * @author Mitchell
 */
public class FutureWriter extends Writer {

  private final LinkedList<Future<String>> orderedFutures = new LinkedList<>();

  private final Writer internalWriter;

  private boolean closed = false;

  public FutureWriter(Writer writer) {
    this.internalWriter = writer;
  }

  public void enqueue(Future<String> future) throws IOException {
    if (this.closed) {
      throw new IOException("Writer is closed");
    }
    this.orderedFutures.add(future);
  }

  @Override
  public void write(final char[] cbuf, final int off, final int len) throws IOException {

    if (this.closed) {
      throw new IOException("Writer is closed");
    }

    final String result = new String(cbuf, off, len);

    if (this.orderedFutures.isEmpty()) {
      this.internalWriter.write(result);
    } else {
      Future<String> future = new Future<String>() {

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
          return false;
        }

        @Override
        public boolean isCancelled() {
          return false;
        }

        @Override
        public boolean isDone() {
          return true;
        }

        @Override
        public String get() {
          return result;
        }

        @Override
        public String get(long timeout, TimeUnit unit) {
          return null;
        }

      };

      this.orderedFutures.add(future);
    }
  }

  @Override
  public void flush() throws IOException {
    for (Future<String> future: this.orderedFutures) {
      try {
        String result = future.get();
        this.internalWriter.write(result);
        this.internalWriter.flush();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (ExecutionException e) {
        throw new IOException(e);
      }
    }
    this.orderedFutures.clear();
  }

  @Override
  public void close() throws IOException {
    this.flush();
    this.internalWriter.close();
    this.closed = true;

  }

}
