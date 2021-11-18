package com.giof71.groovy.reproducer.runnable.randomcachednested.leaking;

import java.util.UUID;
import java.util.function.Supplier;

import com.giof71.groovy.reproducer.RunnerBuilder;
import com.giof71.groovy.reproducer.RunnerType;
import com.giof71.groovy.reproducer.runnable.ScriptCacheImpl;
import com.giof71.groovy.reproducer.runnable.ScriptRandomizerImpl;
import com.giof71.groovy.reproducer.runnable.randomcachednested.RunnerConstant;

public class RandomCachedNestedLeakingScriptRunnerBuilder implements RunnerBuilder {

	@Override
	public RunnerType getRunnerType() {
		return RunnerType.RANDOM_CACHED_NESTED_LEAKING;
	}
	
	private final Supplier<String> scriptSupplier = new Supplier<String>() {

		@Override
		public String get() {
			StringBuilder sb = new StringBuilder();
			sb.append(RunnerConstant.DEFAULT_SCRIPT.name()).append(".apply()");
			sb.append(System.lineSeparator());
			sb.append("String a = \"" + UUID.randomUUID().toString() + "\"");
			return sb.toString();
		}
	};

	@Override
	public Runnable build() {
		return new RandomCachedNestedLeakingScriptRunner(
			new ScriptRandomizerImpl(1000, scriptSupplier), 
			new ScriptCacheImpl(1000, 30));
	}

}
