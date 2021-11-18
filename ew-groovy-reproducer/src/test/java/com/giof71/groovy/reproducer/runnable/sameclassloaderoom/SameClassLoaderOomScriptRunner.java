package com.giof71.groovy.reproducer.runnable.sameclassloaderoom;

import java.util.UUID;

import org.codehaus.groovy.runtime.InvokerHelper;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

class SameClassLoaderOomScriptRunner implements Runnable {
	
	private final GroovyClassLoader gcl;
	
	SameClassLoaderOomScriptRunner(GroovyClassLoader gcl) {
		this.gcl = gcl;
	}
	
	@Override
	public void run() {
		String script = "String a = \"" + UUID.randomUUID().toString() + "\"";
		Class<?> c = gcl.parseClass(script);
		Script s = InvokerHelper.createScript(c, new Binding());
		s.run();
	}
}
