package com.giof71.groovy.reproducer.runnable.randomcached;

import org.codehaus.groovy.runtime.InvokerHelper;

import com.giof71.groovy.reproducer.runnable.ScriptCache;
import com.giof71.groovy.reproducer.runnable.ScriptRandomizer;

import groovy.lang.Binding;
import groovy.lang.Script;

class RandomCachedScriptRunner implements Runnable {

	private final ScriptRandomizer randomizer;
	private final ScriptCache scriptCache;
	
	public RandomCachedScriptRunner(ScriptRandomizer randomizer, ScriptCache scriptCache) {
		this.randomizer = randomizer;
		this.scriptCache = scriptCache;
	}
	
	@Override
	public void run() {
		Class<?> c = scriptCache.getCompiled(randomizer.get());
		Binding binding = new Binding();
		Script scriptClass = InvokerHelper.createScript(c, binding);
		scriptClass.run();
	}
}
