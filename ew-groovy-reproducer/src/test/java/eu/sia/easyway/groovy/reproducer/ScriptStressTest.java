package eu.sia.easyway.groovy.reproducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import eu.sia.easyway.groovy.reproducer.observation.DebugListener;
import eu.sia.easyway.groovy.reproducer.observation.ObservedInterval;
import eu.sia.easyway.groovy.reproducer.observation.Observer;
import eu.sia.easyway.groovy.reproducer.observation.ObserverRunnable;
import eu.sia.easyway.groovy.reproducer.observation.Statistic;
import eu.sia.easyway.groovy.reproducer.observation.StatisticEntry;
import eu.sia.easyway.groovy.reproducer.observation.StatisticListener;
import eu.sia.easyway.groovy.reproducer.observation.impl.ObserverImpl;
import eu.sia.easyway.groovy.reproducer.observation.impl.ObserverRunnableImpl;
import eu.sia.easyway.groovy.reproducer.runnable.AutogeneratedCachedScriptRunner;
import eu.sia.easyway.groovy.reproducer.runnable.AutogeneratedScriptRunnerOneClassLoaderOneScript;
import eu.sia.easyway.groovy.reproducer.runnable.AutogeneratedScriptRunnerRandomScript;
import eu.sia.easyway.groovy.reproducer.runnable.AutogeneratedScriptRunnerSameClassLoaderOom;
import eu.sia.easyway.groovy.reproducer.runnable.AutogeneratedScriptRunnerSameScriptRecompiledEveryTime;
import eu.sia.easyway.groovy.reproducer.runnable.ScriptCacheImpl;
import eu.sia.easyway.groovy.reproducer.runnable.ScriptRandomizerImpl;
import eu.sia.easyway.groovy.reproducer.runnable.SimpleObserverRunnable;
import groovy.lang.GroovyClassLoader;

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
			StringJoiner stringJoiner = new StringJoiner("}; {", "{", "}");
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
	public void t0() throws InterruptedException {
		int numberOfThreads = Integer.parseInt(System.getenv().getOrDefault("NUMBER_OF_THREADS", "4"));
		int durationSec = Integer.parseInt(System.getenv().getOrDefault("DURATION_SEC", "10"));
		boolean statDebugEnabled = Boolean.valueOf(System.getenv().getOrDefault("STAT_DEBUG_ENABLED", "false"));
		System.out.println("Duration: " + durationSec);
		List<Thread> threads = new ArrayList<>();
		GroovyClassLoader gcl = new GroovyClassLoader();
		Observer observer = new ObserverImpl();
		List<ObservedInterval> stats = new ArrayList<>();
		stats.add(new ObservedInterval("S01", Double.valueOf(1.0d)));
		stats.add(new ObservedInterval("S03", Double.valueOf(3.0d)));
		stats.add(new ObservedInterval("S10", Double.valueOf(10.0d)));
		ObserverRunnable observerRunnable = new ObserverRunnableImpl(observer, stats);
		observerRunnable.addStatisticListener(new StatisticListenerToConsole());
		if (statDebugEnabled) {
			observerRunnable.addDebugListener(new DebugToConsole());
		}
		Runnable loopRunner = new SimpleLoopRunner(
			observerRunnable, 
			TimeUnit.MILLISECONDS, 
			1000,
			calcObserverDurationSec(durationSec));
		Thread obsThread = new Thread(loopRunner);
		obsThread.start();
		for (int i = 0; i < numberOfThreads; ++i) {
			// TODO make the runner an app parameter
			Runnable r1 = createWorkerRunnable(observer, new AutogeneratedScriptRunnerRandomScript());
			Runnable r2 = createWorkerRunnable(observer, new AutogeneratedScriptRunnerSameClassLoaderOom(gcl));
			Runnable r3 = createWorkerRunnable(observer, new AutogeneratedScriptRunnerOneClassLoaderOneScript());
			Runnable r4 = createWorkerRunnable(observer, new AutogeneratedScriptRunnerSameScriptRecompiledEveryTime());
			Runnable r5 = createWorkerRunnable(observer, new AutogeneratedCachedScriptRunner(
				new ScriptRandomizerImpl(1000), 
				new ScriptCacheImpl(1000, 30)));
			
			Thread t = new Thread(
				new SimpleLoopRunner(
					r5, 
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
	
	private int calcObserverDurationSec(int testDuration) {
		return testDuration < 0 ? -1 : testDuration + 15;
	}
}
