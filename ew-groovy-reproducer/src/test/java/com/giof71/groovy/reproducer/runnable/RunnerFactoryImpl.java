package com.giof71.groovy.reproducer.runnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.giof71.groovy.reproducer.RunnerBuilder;
import com.giof71.groovy.reproducer.RunnerFactory;
import com.giof71.groovy.reproducer.RunnerType;
import com.giof71.groovy.reproducer.runnable.random.RandomScriptRunnerBuilder;
import com.giof71.groovy.reproducer.runnable.randomcached.RandomCachedScriptRunnerBuilder;
import com.giof71.groovy.reproducer.runnable.sameclassloaderoom.SameClassLoaderOoomBuilder;

public class RunnerFactoryImpl implements RunnerFactory {
	
	private final Map<RunnerType, RunnerBuilder> map = new HashMap<>();
	
	public RunnerFactoryImpl() {
		map.put(RunnerType.RANDOM_SCRIPT, new RandomScriptRunnerBuilder());
		map.put(RunnerType.RANDOM_CACHED, new RandomCachedScriptRunnerBuilder());
		map.put(RunnerType.SAME_CLASSLOADER_OOM, new SameClassLoaderOoomBuilder());
	}

	@Override
	public Runnable getRunner(RunnerType runnerType) {
		RunnerBuilder runnerBuilder = Optional.of(map)
			.map(m -> m.get(runnerType))
			.orElseThrow(() -> new RuntimeException(String.format(
				"Cannot find a %s for %s [%s]",
					RunnerBuilder.class.getSimpleName(),
					RunnerType.class.getSimpleName(),
					runnerType.name())));
		return runnerBuilder.build();
	}

}
