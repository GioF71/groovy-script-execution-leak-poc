package com.giof71.groovy.reproducer.runnable.random;

import java.io.IOException;
import java.util.UUID;

import org.codehaus.groovy.runtime.InvokerHelper;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

class RandomScriptRunner implements Runnable {
	
	@Override
	public void run() {
		try {
			GroovyClassLoader gcl = new GroovyClassLoader();
			String script = "String a = \"" + UUID.randomUUID().toString() + "\"";
			Class<?> c = gcl.parseClass(script);
			gcl.close();
			Script s = InvokerHelper.createScript(c, new Binding());
			s.run();
			InvokerHelper.getMetaRegistry().removeMetaClass(c);
		} catch (IOException exc) {
			throw new RuntimeException(exc);
		}
	}
}
