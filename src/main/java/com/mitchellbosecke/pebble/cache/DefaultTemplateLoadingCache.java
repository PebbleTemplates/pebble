package com.mitchellbosecke.pebble.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class DefaultTemplateLoadingCache implements TemplateLoadingCache {

	private final ConcurrentMap<String, Future<PebbleTemplate>> cache = new ConcurrentHashMap<>();

	@Override
	public PebbleTemplate get(String key, Callable<PebbleTemplate> loadingFunction) throws PebbleException {

		Future<PebbleTemplate> future = cache.get(key);

		if (future == null) {
			FutureTask<PebbleTemplate> futureTask = new FutureTask<PebbleTemplate>(loadingFunction);
			future = cache.putIfAbsent(key, futureTask);

			if (future == null) {
				future = futureTask;
				futureTask.run();
			}
		}
		try {
			return future.get();
		} catch (ExecutionException | InterruptedException e) {
			if (e.getCause() instanceof PebbleException) {
				throw (PebbleException) e.getCause();
			}
			throw new RuntimeException("An error occurred while retrieving from cache");
		}
	}

}