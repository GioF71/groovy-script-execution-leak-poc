package com.giof71.groovy.reproducer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter(value = AccessLevel.PACKAGE)
class StressTestConfig {
	private final int numberOfThreads;
	private final int durationSec;
	private final boolean statisticDebugEnabled;
}
