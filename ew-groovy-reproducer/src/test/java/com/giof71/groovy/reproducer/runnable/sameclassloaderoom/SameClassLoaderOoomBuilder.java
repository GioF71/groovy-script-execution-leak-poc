package com.giof71.groovy.reproducer.runnable.sameclassloaderoom;

import com.giof71.groovy.reproducer.RunnerBuilder;
import com.giof71.groovy.reproducer.RunnerType;

import groovy.lang.GroovyClassLoader;

public class SameClassLoaderOoomBuilder implements RunnerBuilder {
	
	private GroovyClassLoader classLoader = new GroovyClassLoader();

	@Override
	public RunnerType getRunnerType() {
		return RunnerType.RANDOM_SCRIPT;
	}

	@Override
	public Runnable build() {
		return new SameClassLoaderOomScriptRunnerBuilder(classLoader);
	}

}
