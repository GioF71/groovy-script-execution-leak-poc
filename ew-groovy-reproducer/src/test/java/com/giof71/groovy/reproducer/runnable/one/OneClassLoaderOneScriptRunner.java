package com.giof71.groovy.reproducer.runnable.one;

import java.io.IOException;
import java.util.UUID;

import org.codehaus.groovy.runtime.InvokerHelper;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

class OneClassLoaderOneScriptRunner implements Runnable {
	
	private final String scriptText;
	private Script s;
	
	OneClassLoaderOneScriptRunner() {
		this.scriptText = "String a = \"" + UUID.randomUUID().toString() + "\"";
		try {
			GroovyClassLoader gcl = new GroovyClassLoader();
			Class<?> c = gcl.parseClass(scriptText);
			gcl.close();
			s = InvokerHelper.createScript(c, new Binding());
		} catch (IOException exc) {
			throw new RuntimeException(exc);
		}
	}
	
	@Override
	public void run() {
		s.run();
	}
}
