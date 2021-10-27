package eu.sia.easyway.groovy.reproducer.observation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class StatisticEntry {

	@Getter
	private final String name;
	
	@Getter
	private final int count;
	
	@Getter
	private final Double avgSec;
	
	@Getter
	private final Double minSec;
	
	@Getter
	private final Double maxSec;
}
