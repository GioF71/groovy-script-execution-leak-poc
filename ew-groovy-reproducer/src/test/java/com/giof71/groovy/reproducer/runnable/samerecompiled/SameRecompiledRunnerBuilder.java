package com.giof71.groovy.reproducer.runnable.samerecompiled;

import com.giof71.groovy.reproducer.RunnerBuilder;
import com.giof71.groovy.reproducer.RunnerType;

public class SameRecompiledRunnerBuilder implements RunnerBuilder {

	@Override
	public RunnerType getRunnerType() {
		return RunnerType.SAME_RECOMPILED;
	}

	@Override
	public Runnable build() {
		return new SameRecompiledScriptRunner();
	}
}
