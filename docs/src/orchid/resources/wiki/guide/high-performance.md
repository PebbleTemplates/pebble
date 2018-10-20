---
---

# High Performance


## Concurrency
First and foremost, a `PebbleTemplate` object, once compiled, is completely thread safe. As long as the data backing
the template is also thread safe, you can render that single template instance using multiple threads at once.

The actual rendering of a template will typically occur in a sequential manner, from top to bottom. If, however,
you provide an `ExecutorService` to the `PebbleEngine` and make use of the {{ anchor('parallel') }} tag, you can
have multiple threads render different sections of your template at one time. This is especially useful if one section
of your template is costly and will otherwise block the rendering of the rest of the template.

## Streaming
The use of the {{ anchor('flush') }} tag can be used to stream the rendered output as it's being rendered.
This can significantly improve latency.

## Performance Pitfalls
- It is typically okay for a block to use the `flush` tag unless the contents of that block is being rendered using the {{ anchor('block') }} function. Typically the flush tag will flush to the `Writer` that you provided but the block function internally uses it's own `StringWriter` and therefore flushing will do no good.