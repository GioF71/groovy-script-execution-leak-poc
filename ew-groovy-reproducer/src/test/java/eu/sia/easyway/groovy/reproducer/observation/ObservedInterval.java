package eu.sia.easyway.groovy.reproducer.observation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ObservedInterval {
	private final String name;
	private final Double nSec;
}
