package eu.sia.easyway.groovy.reproducer.observation.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import eu.sia.easyway.groovy.reproducer.observation.DebugListener;
import eu.sia.easyway.groovy.reproducer.observation.ObservedInterval;
import eu.sia.easyway.groovy.reproducer.observation.Observer;
import eu.sia.easyway.groovy.reproducer.observation.ObserverRunnable;
import eu.sia.easyway.groovy.reproducer.observation.Statistic;
import eu.sia.easyway.groovy.reproducer.observation.StatisticEntry;
import eu.sia.easyway.groovy.reproducer.observation.StatisticListener;
import eu.sia.easyway.groovy.reproducer.stat.Metric;

public class ObserverRunnableImpl implements ObserverRunnable {

	private static final MetricComparator metricComparator = new MetricComparator();
	private final Observer observer;
	private final List<ObservedInterval> statListLowerToGreater = new ArrayList<>();
	private final List<DebugListener> debugListenerList = new ArrayList<>();
	private final List<StatisticListener> statisticListenerList = new ArrayList<>();

	public ObserverRunnableImpl(Observer observer, List<ObservedInterval> statListLowerToGreater) {
		this.observer = observer;
		this.statListLowerToGreater.addAll(validate(statListLowerToGreater));
	}

	@Override
	public void addDebugListener(DebugListener listener) {
		debugListenerList.add(listener);
	}
	
	@Override
	public void addStatisticListener(StatisticListener listener) {
		statisticListenerList.add(listener);
	}

	private void debug(String format, Object... args) {
		for (DebugListener d : debugListenerList) {
			d.debug(format, args);
		}
	}
	
	private void onStatistic(Statistic statistic) {
		for (StatisticListener d : statisticListenerList) {
			d.onStatistic(statistic);
		}
	}

	private List<ObservedInterval> validate(List<ObservedInterval> statListLowerToGreater) {
		if (statListLowerToGreater == null || statListLowerToGreater.size() == 0) {
			throw new RuntimeException("Empty list");
		}
		double current = 0.0f;
		for (ObservedInterval o : statListLowerToGreater) {
			if (o == null || o.getNumberOfSeconds() == null || o.getNumberOfSeconds().doubleValue() < current) {
				throw new RuntimeException("Sequence is not strictly increasing");
			}
			current = o.getNumberOfSeconds();
		}
		return statListLowerToGreater;
	}

	@Override
	public void run() {
		String runId = UUID.randomUUID().toString();
		long now = System.nanoTime();
		Double max = statListLowerToGreater.get(statListLowerToGreater.size() - 1).getNumberOfSeconds();
		List<Metric> metricList = observer.getMetricList(max, true);
		debug(String.format("[%s] Metric count = %d", runId, metricList.size()));
		metricList.sort(metricComparator);
		List<PerfCalculator> perfCalculatorList = new ArrayList<>();
		int lastUpperBound = metricList.size();
		for (int statIndex = 0; statIndex < statListLowerToGreater.size(); ++statIndex) {
			ObservedInterval currentObserverInterval = statListLowerToGreater.get(statIndex);
			String currentStatName = currentObserverInterval.getName();
			double deltaSec = currentObserverInterval.getNumberOfSeconds();
			long limitNano = now - (long) ((double) deltaSec * 1000000000.0f);
			Metric lowerLimit = new Metric(limitNano, 0);
			int lowerIndex = Collections.binarySearch(metricList, lowerLimit, metricComparator);
			if (lowerIndex < 0) {
				lowerIndex = Math.abs(lowerIndex) - 1;
			}
			PerfCalculator perfCalculator = perfCalculatorList.size() == 0 
					? new PerfCalculator(currentStatName)
					: new PerfCalculator(currentStatName, perfCalculatorList.get(perfCalculatorList.size() - 1));
			int cycleUpperLimit = lastUpperBound;
			debug(String.format("[%s] [%s] Accumulating range [%d,%d) over %s", 
					runId, 
					currentStatName, 
					lowerIndex, 
					cycleUpperLimit,
					perfCalculatorList.size() == 0 ? "none" : "previous_stat"));
			for (int i = lowerIndex; i < metricList.size(); ++i) {
				perfCalculator.add(metricList.get(i).getDeltaNanosec());
			}
			perfCalculatorList.add(perfCalculator);
			lastUpperBound = lowerIndex;
		}
		print(perfCalculatorList);
	}

	private void print(List<PerfCalculator> perfCalculatorList) {
		StatisticImpl statistic = new StatisticImpl();
		for (PerfCalculator perfCalculator : perfCalculatorList) {
			StatisticEntry entry = perfCalculator.getStatisticEntry();
			statistic.add(entry);
		}
		onStatistic(statistic);
	}
}
