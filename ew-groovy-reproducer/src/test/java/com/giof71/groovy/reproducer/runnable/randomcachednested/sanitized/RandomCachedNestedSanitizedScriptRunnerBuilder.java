package com.giof71.groovy.reproducer.runnable.randomcachednested.sanitized;

import java.util.UUID;
import java.util.function.Supplier;

import com.giof71.groovy.reproducer.RunnerBuilder;
import com.giof71.groovy.reproducer.RunnerType;
import com.giof71.groovy.reproducer.runnable.ScriptCacheImpl;
import com.giof71.groovy.reproducer.runnable.ScriptRandomizerImpl;

public class RandomCachedNestedSanitizedScriptRunnerBuilder implements RunnerBuilder {

	@Override
	public RunnerType getRunnerType() {
		return RunnerType.RANDOM_CACHED_NESTED_LEAKING;
	}
	
	private final Supplier<String> scriptSupplier = new Supplier<String>() {

		@Override
		public String get() {
			StringBuilder sb = new StringBuilder();
			sb.append("String a = \"" + UUID.randomUUID().toString() + "\"");
			return sb.toString();
		}
	};

	@Override
	public Runnable build() {
		return new RandomCachedNestedSanitizedScriptRunner(
			new ScriptRandomizerImpl(1000, scriptSupplier), 
			new ScriptCacheImpl(1000, 1));
	}

}
