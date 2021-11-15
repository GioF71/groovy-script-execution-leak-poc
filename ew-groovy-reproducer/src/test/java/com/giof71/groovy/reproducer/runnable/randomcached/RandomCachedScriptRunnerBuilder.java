package com.giof71.groovy.reproducer.runnable.randomcached;

import com.giof71.groovy.reproducer.RunnerBuilder;
import com.giof71.groovy.reproducer.RunnerType;
import com.giof71.groovy.reproducer.runnable.ScriptCacheImpl;
import com.giof71.groovy.reproducer.runnable.ScriptRandomizerImpl;

public class RandomCachedScriptRunnerBuilder implements RunnerBuilder {

	@Override
	public RunnerType getRunnerType() {
		return RunnerType.RANDOM_CACHED;
	}

	@Override
	public Runnable build() {
		return new RandomCachedScriptRunner(
			new ScriptRandomizerImpl(1000), 
			new ScriptCacheImpl(1000, 30));
	}
}
