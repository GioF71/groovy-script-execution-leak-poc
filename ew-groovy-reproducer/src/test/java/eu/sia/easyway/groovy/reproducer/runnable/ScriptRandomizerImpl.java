package eu.sia.easyway.groovy.reproducer.runnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ScriptRandomizerImpl implements ScriptRandomizer {
	
	private final Random r = new Random();
	private final List<String> list = new ArrayList<>();
	
	public ScriptRandomizerImpl(int size) {
		for (int i = 0; i < size; ++i) {
			String s = "String a = \"" + UUID.randomUUID().toString() + "\"";
			list.add(s);
		}
	}

	@Override
	public String get() {
		float f = r.nextFloat();
		int position = (int) (f * (float) list.size());
		return list.get(position);
	}
}
