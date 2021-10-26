package eu.sia.easyway.groovy.reproducer.runnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.sia.easyway.groovy.reproducer.stat.Metric;

public class ObserverRunnable implements Runnable {
	
	private final Observer observer;
	private final List<Double> statListLowerToGreater = new ArrayList<>();
	
	private final Comparator<Metric> metricComparator = new Comparator<Metric>() {

		@Override
		public int compare(Metric o1, Metric o2) {
			return Long.compare(o1.getNanoTime(), o2.getNanoTime());
		}
		
	};
	
	private final Comparator<Metric> reverseMetricComparator = (o1, o2) -> -1 * metricComparator.compare(o1, o2);
	
	public ObserverRunnable(Observer observer, List<Double> statListLowerToGreater) {
		this.observer = observer;
		this.statListLowerToGreater.addAll(validate(statListLowerToGreater));
	}
	
	private List<Double> validate(List<Double> statListLowerToGreater) {
		if (statListLowerToGreater == null || statListLowerToGreater.size() == 0) {
			throw new RuntimeException("Empty list");
		}
		double current = 0.0f;
		for (Double d : statListLowerToGreater) {
			if (d == null || d.doubleValue() < current) {
				throw new RuntimeException("Sequence is not growing");
			}
		}
		return statListLowerToGreater;
	}
	
	class PerfCalculator {
		
		private int count = 0;
		private double sumSec = 0.0f;
		private Double minSec = null;
		private Double maxSec = null;
		
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
	}

	@Override
	public void run() {
		List<String> resultList = observer.get();
		for (String resultEntry : resultList) {
			System.out.println(resultEntry);
		}
		// 10, 5, 1 sec
		Double max = statListLowerToGreater.get(statListLowerToGreater.size() - 1);
		List<Metric> metricList = observer.getMetricList(max, true);
		metricList.sort(reverseMetricComparator);
		Long lowestNano = metricList.size() > 0 ? metricList.get(0).getNanoTime() : null;
		if (lowestNano != null) {
			List<PerfCalculator> perfCalculatorList = new ArrayList<>();
			for (int statIndex = 0; statIndex < statListLowerToGreater.size(); ++statIndex) {
				double deltaSec = statListLowerToGreater.get(statIndex);
				Metric limit = new Metric(lowestNano + (long) ((double) deltaSec * 1000000000.0f), 0);
				int limitIndex = Collections.binarySearch(metricList, limit, reverseMetricComparator);
			}
		}
	}
}
