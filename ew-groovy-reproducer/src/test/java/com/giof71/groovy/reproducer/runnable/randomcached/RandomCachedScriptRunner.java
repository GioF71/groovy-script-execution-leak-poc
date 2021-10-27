package com.giof71.groovy.reproducer.runnable.randomcached;

import com.giof71.groovy.reproducer.runnable.ScriptCache;
import com.giof71.groovy.reproducer.runnable.ScriptRandomizer;

import groovy.lang.Script;

public class RandomCachedScriptRunner implements Runnable {

	private final ScriptRandomizer randomizer;
	private final ScriptCache scriptCache;
	
	public RandomCachedScriptRunner(ScriptRandomizer randomizer, ScriptCache scriptCache) {
		this.randomizer = randomizer;
		this.scriptCache = scriptCache;
	}
	
	@Override
	public void run() {
		Script s = scriptCache.getCompiled(randomizer.get());
		s.run();
	}
}
