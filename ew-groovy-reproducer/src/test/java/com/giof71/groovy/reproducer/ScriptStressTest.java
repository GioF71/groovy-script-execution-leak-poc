package com.giof71.groovy.reproducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.giof71.groovy.reproducer.observation.DebugListener;
import com.giof71.groovy.reproducer.observation.ObservedInterval;
import com.giof71.groovy.reproducer.observation.Observer;
import com.giof71.groovy.reproducer.observation.ObserverRunnable;
import com.giof71.groovy.reproducer.observation.Statistic;
import com.giof71.groovy.reproducer.observation.StatisticEntry;
import com.giof71.groovy.reproducer.observation.StatisticListener;
import com.giof71.groovy.reproducer.observation.impl.ObserverImpl;
import com.giof71.groovy.reproducer.observation.impl.ObserverRunnableImpl;
import com.giof71.groovy.reproducer.runnable.RunnerFactoryImpl;
import com.giof71.groovy.reproducer.runnable.SimpleObserverRunnable;

public class ScriptStressTest {
	
	private Runnable createWorkerRunnable(Observer observer, Runnable runnable) {
		return new SimpleObserverRunnable(observer, runnable);
	}
	
	class DebugToConsole implements DebugListener {

		@Override
		public void debug(String format, Object... args) {
			System.out.println(String.format(format, args));
		}
	}
	
	class StatisticListenerToConsole implements StatisticListener {

		@Override
		public void onStatistic(Statistic statistic) {
			StringJoiner stringJoiner = new StringJoiner("} {", "{", "}");
			for (StatisticEntry entry : statistic) {
				stringJoiner.add(String.format("Name [%s] Cnt [%d] Avg [%.3f] Min [%.3f] Max [%.3f]",
					entry.getName(), 
					entry.getCount(), 
					toMilli(getAvgMilliSec(entry)),
					toMilli(entry.getMinSec()), 
					toMilli(entry.getMaxSec())));
			}
			System.out.println(stringJoiner.toString());
		}
		
		private Double toMilli(Double valueSec) {
			return Optional.ofNullable(valueSec).map(v -> (v.doubleValue() * (1000.0f))).orElse(null);
		}

		private Double getAvgMilliSec(StatisticEntry p) {
			if (p.getCount() > 0) {
				return toMilli(p.getAvgSec());
			}
			return null;
		}
	}
	
	@Test
	public void t0() throws Exception {
		RunnerFactory runnerFactory = new RunnerFactoryImpl();
		RunnerType runnerType = RunnerType.valueOf(System.getenv("RUNNER_TYPE"));
		int numberOfThreads = Integer.parseInt(System.getenv().getOrDefault("NUMBER_OF_THREADS", "4"));
		int durationSec = Integer.parseInt(System.getenv().getOrDefault("DURATION_SEC", "10"));
		System.out.println(RunnerType.class.getSimpleName() + ": " + runnerType.name());
		System.out.println("Test duration: " + durationSec);
		List<Thread> threads = new ArrayList<>();
		Observer observer = new ObserverImpl();
		List<ObservedInterval> stats = createObservationIntervalList();
		ObserverRunnable observerRunnable = createObserverRunnable(observer, stats);
		Runnable observerLoopRunner = new SimpleLoopRunner(
			observerRunnable, 
			TimeUnit.MILLISECONDS, 
			1000,
			calcObserverDurationSec(durationSec));
		Thread obsThread = new Thread(observerLoopRunner);
		obsThread.start();
		for (int i = 0; i < numberOfThreads; ++i) {
			Runnable runner = createWorkerRunnable(observer, runnerFactory.getRunner(runnerType));
			Thread t = new Thread(
				new SimpleLoopRunner(
					runner, 
					TimeUnit.MICROSECONDS, 
					500, 
					durationSec));
			threads.add(t);
			t.start();
		}
		for (Thread t : threads) {
			t.join();
		}
		obsThread.join();
	}

	private ObserverRunnable createObserverRunnable(
			Observer observer, 
			List<ObservedInterval> observedIntervalList) {
		boolean statDebugEnabled = Boolean.valueOf(System.getenv().getOrDefault("STAT_DEBUG_ENABLED", "false"));
		ObserverRunnable observerRunnable = new ObserverRunnableImpl(observer, observedIntervalList);
		observerRunnable.addStatisticListener(new StatisticListenerToConsole());
		if (statDebugEnabled) {
			observerRunnable.addDebugListener(new DebugToConsole());
		}
		return observerRunnable;
	}

	private List<ObservedInterval> createObservationIntervalList() {
		List<ObservedInterval> stats = new ArrayList<>();
		stats.add(new ObservedInterval("S01", Double.valueOf(1.0d)));
		stats.add(new ObservedInterval("S03", Double.valueOf(3.0d)));
		stats.add(new ObservedInterval("S10", Double.valueOf(10.0d)));
		return stats;
	}
	
	private int calcObserverDurationSec(int testDuration) {
		return testDuration < 0 ? -1 : testDuration + 15;
	}
}
