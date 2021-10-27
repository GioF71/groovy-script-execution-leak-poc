package eu.sia.easyway.groovy.reproducer.observation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import eu.sia.easyway.groovy.reproducer.stat.Metric;

public class ObserverRunnable implements Runnable {
	
	private final Observer observer;
	private final List<ObservedInterval> statListLowerToGreater = new ArrayList<>();
	
	private final Comparator<Metric> metricComparator = new Comparator<Metric>() {

		@Override
		public int compare(Metric o1, Metric o2) {
			return Long.compare(o1.getNanoTime(), o2.getNanoTime());
		}
		
	};
	
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
			if (o == null || o.getNSec() == null || o.getNSec().doubleValue() < current) {
				throw new RuntimeException("Sequence is not growing");
			}
		}
		return statListLowerToGreater;
	}
	
	class PerfCalculator {
		
		private final String name;
		private int count = 0;
		private double sumSec = 0.0f;
		private Double minSec = null;
		private Double maxSec = null;
		
		PerfCalculator(String name) {
			this.name = name;
		}
		
		PerfCalculator(String name, PerfCalculator perfCalculator) {
			this.name = name;
			this.count = perfCalculator.getCount();
			this.sumSec = perfCalculator.getSumSec();
			this.minSec = perfCalculator.getMinSec();
			this.maxSec = perfCalculator.getMaxSec();
		}
		
		void add(long deltaNanoSec) {
			double deltaSec = ((double) deltaNanoSec) / (1000000000.0f);
			if (minSec == null || minSec.doubleValue() > deltaSec) {
				minSec = deltaSec;
			}
			if (maxSec == null || maxSec.doubleValue() < deltaSec) {
				maxSec = deltaSec;
			}
			sumSec += deltaSec;
			++count;
		}
		
		String getName() {
			return name;
		}
		
		int getCount() {
			return count;
		}

		double getSumSec() {
			return sumSec;
		}

		Double getMinSec() {
			return minSec;
		}

		Double getMaxSec() {
			return maxSec;
		}

		double getAvg() {
			return sumSec / ((double) count);
		}
	}

	@Override
	public void run() {
//		List<String> resultList = observer.get();
//		for (String resultEntry : resultList) {
//			System.out.println(resultEntry);
//		}
		// 10, 5, 1 sec
		Double max = statListLowerToGreater.get(statListLowerToGreater.size() - 1).getNSec();
		List<Metric> metricList = observer.getMetricList(max, true);
		// TODO must sort in reverse!
		metricList.sort(metricComparator);
		Long lowestNano = metricList.size() > 0 ? metricList.get(0).getNanoTime() : null;
		if (lowestNano != null) {
			int initialIndex = 0;
			List<PerfCalculator> perfCalculatorList = new ArrayList<>();
			for (int statIndex = 0; statIndex < statListLowerToGreater.size(); ++statIndex) {
				ObservedInterval currentDelta = statListLowerToGreater.get(statIndex);
				double deltaSec = currentDelta.getNSec();
				long limitNano = lowestNano + (long) ((double) deltaSec * 1000000000.0f);
				Metric limit = new Metric(limitNano, 0);
				int limitIndex = Math.abs(Collections.binarySearch(metricList, limit, metricComparator));
				PerfCalculator perfCalculator = perfCalculatorList.size() == 0 
					? new PerfCalculator(currentDelta.getName()) 
					: new PerfCalculator(currentDelta.getName(), perfCalculatorList.get(perfCalculatorList.size() - 1));
				int upperBound = Math.min(limitIndex, metricList.size());
				for (int i = initialIndex; i < upperBound; ++i) {
					perfCalculator.add(metricList.get(i).getDeltaNanosec());
				}
				perfCalculatorList.add(perfCalculator);
				initialIndex = limitIndex + 1;
			}
			StringJoiner stringJoiner = new StringJoiner("}; {", "{", "}");
			for (PerfCalculator perfCalculator : perfCalculatorList) {
				stringJoiner.add(String.format(
					"Name [%s] Cnt [%d] Avg [%.3f] Min [%.3f] Max [%.3f]",
						perfCalculator.getName(),
						perfCalculator.getCount(),
						perfCalculator.getAvg() * (1000.0f),
						toMilli(perfCalculator.getMinSec()),
						toMilli(perfCalculator.getMaxSec())));
			}
			System.out.println(stringJoiner.toString());
		}
	}
	
	private Double toMilli(Double valueSec) {
		return Optional.ofNullable(valueSec)
			.map(v -> (v.doubleValue() * (1000.0f)))
			.orElse(null);
	}
}
