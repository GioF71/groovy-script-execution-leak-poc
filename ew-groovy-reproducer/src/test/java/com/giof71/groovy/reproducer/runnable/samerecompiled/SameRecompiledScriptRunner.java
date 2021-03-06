package com.giof71.groovy.reproducer.runnable.samerecompiled;

import java.io.IOException;
import java.util.UUID;

import org.codehaus.groovy.runtime.InvokerHelper;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

class SameRecompiledScriptRunner implements Runnable {
	
	private final String scriptText = "String a = \"" + UUID.randomUUID().toString() + "\"";
	
	@Override
	public void run() {
		try {
			GroovyClassLoader gcl = new GroovyClassLoader();
			Class<?> c = gcl.parseClass(scriptText);
			gcl.close();
			Script s = InvokerHelper.createScript(c, new Binding());
			s.run();
		} catch (IOException exc) {
			throw new RuntimeException(exc);
		}
	}
}
