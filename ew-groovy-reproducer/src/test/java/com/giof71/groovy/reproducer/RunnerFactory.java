package com.giof71.groovy.reproducer;

public interface RunnerFactory {
	Runnable getRunner(RunnerType runnerType);
}
