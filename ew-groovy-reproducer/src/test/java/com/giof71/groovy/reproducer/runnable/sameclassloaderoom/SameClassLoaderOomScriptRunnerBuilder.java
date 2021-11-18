package com.giof71.groovy.reproducer.runnable.sameclassloaderoom;

import com.giof71.groovy.reproducer.RunnerBuilder;
import com.giof71.groovy.reproducer.RunnerType;

import groovy.lang.GroovyClassLoader;

public class SameClassLoaderOomScriptRunnerBuilder implements RunnerBuilder {
	
	private GroovyClassLoader classLoader = new GroovyClassLoader();

	@Override
	public RunnerType getRunnerType() {
		return RunnerType.SAME_CLASSLOADER_OOM;
	}

	@Override
	public Runnable build() {
		return new SameClassLoaderOomScriptRunner(classLoader);
	}

}
