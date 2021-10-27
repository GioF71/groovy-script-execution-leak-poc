package com.giof71.groovy.reproducer.observation.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.giof71.groovy.reproducer.observation.Statistic;
import com.giof71.groovy.reproducer.observation.StatisticEntry;

class StatisticImpl implements Statistic {
	
	private List<StatisticEntry> list;

	private List<StatisticEntry> getList() {
		if (list == null) {
			list = new ArrayList<>();
		}
		return list;
	}
	
	void add(StatisticEntry entry) {
		getList().add(entry);
	}

	@Override
	public Iterator<StatisticEntry> iterator() {
		return Optional.ofNullable(list).orElse(Collections.emptyList()).iterator();
	}

}
