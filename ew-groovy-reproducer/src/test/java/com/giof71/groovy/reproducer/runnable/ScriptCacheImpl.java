package com.giof71.groovy.reproducer.runnable;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import groovy.lang.GroovyClassLoader;

public class ScriptCacheImpl implements ScriptCache {
	
	private Cache<String, Class<?>> scriptCache;

	public ScriptCacheImpl(int maxSize, int expirationSec) {
		scriptCache = CacheBuilder.newBuilder()
			.maximumSize(maxSize)
			.expireAfterAccess(expirationSec, TimeUnit.SECONDS)
			.build(new CacheLoader<String, Class<?>>() {
				@Override
				public Class<?> load(String arg0) throws Exception {
					return compile(arg0);
				}});
	}
	
	@Override
	public Class<?> getCompiled(String scriptText) {
		try {
			return scriptCache.get(scriptText, new Callable<Class<?>>() {

				@Override
				public Class<?> call() throws Exception {
					return compile(scriptText);
				}});
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private Class<?> compile(String scriptText) {
		try {
			GroovyClassLoader gcl = new GroovyClassLoader();
			Class<?> c = gcl.parseClass(scriptText);
			gcl.close();
			return c;
		} catch (IOException exc) {
			throw new RuntimeException(exc);
		}
	}
}
