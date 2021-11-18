package com.giof71.groovy.reproducer.runnable.randomcachednested;

import org.codehaus.groovy.runtime.InvokerHelper;

import com.giof71.groovy.reproducer.runnable.ScriptCache;
import com.giof71.groovy.reproducer.runnable.ScriptRandomizer;

import groovy.lang.Binding;
import groovy.lang.Script;

public class RandomCachedNestedScriptRunner implements Runnable {

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
	
	public RandomCachedNestedScriptRunner(ScriptRandomizer randomizer, ScriptCache scriptCache) {
		this.randomizer = randomizer;
		this.scriptCache = scriptCache;
	}
	
	@Override
	public void run() {
		Class<?> c = scriptCache.getCompiled(randomizer.get());
		DefaultScript defaultScript = new LocalDefaultScript(scriptCache);
		Binding binding = new Binding();
		binding.setProperty(RunnerConstant.DEFAULT_SCRIPT.name(), defaultScript);
		Script scriptClass = InvokerHelper.createScript(c, binding);
		scriptClass.run();
	}
}
