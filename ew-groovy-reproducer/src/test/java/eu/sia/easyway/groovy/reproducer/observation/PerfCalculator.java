package eu.sia.easyway.groovy.reproducer.observation;

class PerfCalculator {

	private final String name;
	private int count = 0;
	private Double sumSec = Double.valueOf(0.0f);
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
