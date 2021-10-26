package eu.sia.easyway.groovy.reproducer.runnable;

import groovy.lang.Script;

public interface ScriptCache {
	Script getCompiled(String scriptText);
}
