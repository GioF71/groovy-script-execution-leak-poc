package com.giof71.groovy.reproducer.observation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ObservedInterval {

	@Getter
	private final String name;
	
	@Getter
	private final Double numberOfSeconds;
}
