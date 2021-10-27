package com.giof71.groovy.reproducer.observation.impl;

import java.util.Comparator;

import com.giof71.groovy.reproducer.stat.Metric;

class MetricComparator implements Comparator<Metric> {
	
	@Override
	public int compare(Metric o1, Metric o2) {
		return Long.compare(o1.getNanoTime(), o2.getNanoTime());
	}
}
