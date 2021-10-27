package com.giof71.groovy.reproducer;

public interface RunnerBuilder {
	RunnerType getRunnerType();
	Runnable build();
}
