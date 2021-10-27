package com.giof71.groovy.reproducer.runnable;

import groovy.lang.Script;

public interface ScriptCache {
	Script getCompiled(String scriptText);
}
