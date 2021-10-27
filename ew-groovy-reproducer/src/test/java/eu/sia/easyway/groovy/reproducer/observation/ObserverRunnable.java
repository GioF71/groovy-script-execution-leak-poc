package eu.sia.easyway.groovy.reproducer.observation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

import eu.sia.easyway.groovy.reproducer.stat.Metric;

public class ObserverRunnable implements Runnable {

	private final Observer observer;
	private final List<ObservedInterval> statListLowerToGreater = new ArrayList<>();

	public ObserverRunnable(Observer observer, List<ObservedInterval> statListLowerToGreater) {
		this.observer = observer;
		this.statListLowerToGreater.addAll(validate(statListLowerToGreater));
	}

	private List<ObservedInterval> validate(List<ObservedInterval> statListLowerToGreater) {
		if (statListLowerToGreater == null || statListLowerToGreater.size() == 0) {
			throw new RuntimeException("Empty list");
		}
		double current = 0.0f;
		for (ObservedInterval o : statListLowerToGreater) {
			if (o == null || o.getNumberOfSeconds() == null || o.getNumberOfSeconds().doubleValue() < current) {
				throw new RuntimeException("Sequence is not growing");
			}
		}
		return statListLowerToGreater;
	}

	@Override
	public void run() {
		String runId = UUID.randomUUID().toString();
		long now = System.nanoTime();
		Double max = statListLowerToGreater.get(statListLowerToGreater.size() - 1).getNumberOfSeconds();
		List<Metric> metricList = observer.getMetricList(max, true);
		System.out.println(String.format("[%s] Metric count = %d", runId, metricList.size()));
		
		metricList.sort(new MetricComparator());
		List<PerfCalculator> perfCalculatorList = new ArrayList<>();
		//int lastUpperBound = -1;
		for (int statIndex = 0; statIndex < statListLowerToGreater.size(); ++statIndex) {
			//int upperBound = lastUpperBound == -1 ? metricList.size() : lastUpperBound;
			ObservedInterval currentObserverInterval = statListLowerToGreater.get(statIndex);
			String currentStatName = currentObserverInterval.getName();
			double deltaSec = currentObserverInterval.getNumberOfSeconds();
			long limitNano = now - (long) ((double) deltaSec * 1000000000.0f);
			Metric lowerLimit = new Metric(limitNano, 0);
			int lowerIndex = Collections.binarySearch(metricList, lowerLimit, new MetricComparator());
			if (lowerIndex < 0) {
				lowerIndex = Math.abs(lowerIndex) - 1;
			}
//			PerfCalculator perfCalculator = perfCalculatorList.size() == 0 
//					? new PerfCalculator(currentStatName)
//					: new PerfCalculator(currentStatName, perfCalculatorList.get(perfCalculatorList.size() - 1));
			PerfCalculator perfCalculator = new PerfCalculator(currentStatName);

			int cycleUpperLimit = metricList.size(); // unoptimized
					
			//int cycleUpperLimit = Math.min(metricList.size(), metricList.size());
			//int cycleUpperLimit = lastUpperBound == -1 ? metricList.size() : lastUpperBound;
			System.out.println(String.format("[%s] [%s] Accumulating from index [%d] to limit [%d]", runId, currentStatName, lowerIndex, cycleUpperLimit));
			//for (int i = lowerIndex; i < cycleUpperLimit; ++i) {
			for (int i = lowerIndex; i < metricList.size(); ++i) {
				perfCalculator.add(metricList.get(i).getDeltaNanosec());
			}
			perfCalculatorList.add(perfCalculator);
			//lastUpperBound = lowerIndex;
		}
		print(perfCalculatorList);
	}

	private void print(List<PerfCalculator> perfCalculatorList) {
		StringJoiner stringJoiner = new StringJoiner("}; {", "{", "}");
		for (PerfCalculator perfCalculator : perfCalculatorList) {
			stringJoiner.add(String.format("Name [%s] Cnt [%d] Avg [%.3f] Min [%.3f] Max [%.3f]",
				perfCalculator.getName(), 
				perfCalculator.getCount(), 
				toMilli(getAvg(perfCalculator)),
				toMilli(perfCalculator.getMinSec()), 
				toMilli(perfCalculator.getMaxSec())));
		}
		System.out.println(stringJoiner.toString());
	}

	private Double toMilli(Double valueSec) {
		return Optional.ofNullable(valueSec).map(v -> (v.doubleValue() * (1000.0f))).orElse(null);
	}

	private Double getAvg(PerfCalculator p) {
		if (p.getCount() > 0) {
			return toMilli(p.getAvg());
		}
		return null;
	}
}
