package com.giof71.groovy.reproducer.runnable.randomcachednested.sanitized;

import org.codehaus.groovy.runtime.InvokerHelper;

import com.giof71.groovy.reproducer.runnable.ScriptCache;
import com.giof71.groovy.reproducer.runnable.ScriptRandomizer;
import com.giof71.groovy.reproducer.runnable.randomcachednested.DefaultScript;

import groovy.lang.Binding;
import groovy.lang.Script;

public class RandomCachedNestedSanitizedScriptRunner implements Runnable {

	private final ScriptRandomizer randomizer;
	private final ScriptCache scriptCache;
	
	private final String defaultScriptText = "String s = \"default_script\"";
	
	class LocalDefaultScript implements DefaultScript {
		
		private final ScriptCache scriptCache;
		
		LocalDefaultScript(ScriptCache scriptCache) {
			this.scriptCache = scriptCache;
		}
		
		@Override
		public void apply() {
			Class<?> defaultScriptClazz = scriptCache.getCompiled(defaultScriptText);
			Binding binding = new Binding();
			Script scriptClass = InvokerHelper.createScript(defaultScriptClazz, binding);
			scriptClass.run();
		}
	}
	
	public RandomCachedNestedSanitizedScriptRunner(ScriptRandomizer randomizer, ScriptCache scriptCache) {
		this.randomizer = randomizer;
		this.scriptCache = scriptCache;
	}
	
	@Override
	public void run() {
		// launch default script
		Class<?> defaultScriptClass = scriptCache.getCompiled(defaultScriptText);
		Script defaultScript = InvokerHelper.createScript(defaultScriptClass, new Binding());
		defaultScript.run();
		
		// then run user script
		Class<?> c = scriptCache.getCompiled(randomizer.get());
		Script scriptClass = InvokerHelper.createScript(c, new Binding());
		scriptClass.run();
	}
}
