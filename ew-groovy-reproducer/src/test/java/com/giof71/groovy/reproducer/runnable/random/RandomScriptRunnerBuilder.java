package com.giof71.groovy.reproducer.runnable.random;

import com.giof71.groovy.reproducer.RunnerBuilder;
import com.giof71.groovy.reproducer.RunnerType;

public class RandomScriptRunnerBuilder implements RunnerBuilder {

	@Override
	public RunnerType getRunnerType() {
		return RunnerType.RANDOM_SCRIPT;
	}

	@Override
	public Runnable build() {
		return new RandomScriptRunner();
	}

}
