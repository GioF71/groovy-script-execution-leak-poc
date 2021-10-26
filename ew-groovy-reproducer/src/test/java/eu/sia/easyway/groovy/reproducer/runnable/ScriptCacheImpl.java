package eu.sia.easyway.groovy.reproducer.runnable;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.codehaus.groovy.runtime.InvokerHelper;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

public class ScriptCacheImpl implements ScriptCache {
	
	private Cache<String, Script> scriptCache;

	public ScriptCacheImpl(int maxSize, int expirationSec) {
		scriptCache = CacheBuilder.newBuilder()
			.maximumSize(maxSize)
			.expireAfterAccess(expirationSec, TimeUnit.SECONDS)
			.build(new CacheLoader<String, Script>() {
				@Override
				public Script load(String arg0) throws Exception {
					return compile(arg0);
				}});
	}
	
	@Override
	public Script getCompiled(String scriptText) {
		try {
			return scriptCache.get(scriptText);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private Script compile(String scriptText) {
		try {
			GroovyClassLoader gcl = new GroovyClassLoader();
			Class<?> c = gcl.parseClass(scriptText);
			gcl.close();
			return InvokerHelper.createScript(c, new Binding());
		} catch (IOException exc) {
			throw new RuntimeException(exc);
		}
	}
}
