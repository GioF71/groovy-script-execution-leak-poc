package com.giof71.groovy.reproducer.runnable.one;

import com.giof71.groovy.reproducer.RunnerBuilder;
import com.giof71.groovy.reproducer.RunnerType;

public class OneClassLoaderOneScriptRunnerBuilder implements RunnerBuilder {

	@Override
	public RunnerType getRunnerType() {
		return RunnerType.ONE_CLASSLOADER;
	}

	@Override
	public Runnable build() {
		return new OneClassLoaderOneScriptRunner();
	}
}
