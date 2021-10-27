package com.giof71.groovy.reproducer.observation;

public interface ObserverRunnable extends Runnable {
	void addDebugListener(DebugListener listener);
	void addStatisticListener(StatisticListener listener);
}
