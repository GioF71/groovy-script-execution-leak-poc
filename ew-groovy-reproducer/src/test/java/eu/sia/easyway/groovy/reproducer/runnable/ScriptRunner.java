package eu.sia.easyway.groovy.reproducer.runnable;

import java.io.IOException;

import org.codehaus.groovy.runtime.InvokerHelper;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;

public class ScriptRunner implements Runnable {
	
	private final String script;
	private final Binding binding;
	
	public ScriptRunner(String script, Binding binding) {
		this.script = script;
		this.binding = binding;
	}

	@Override
	public void run() {
		try {
			GroovyClassLoader gcl = new GroovyClassLoader();
			Class<?> c = gcl.parseClass(script);	
			Script s = InvokerHelper.createScript(c, binding);
			s.run();
			gcl.close();
		} catch (IOException exc) {
			throw new RuntimeException(exc);
		}
	}

}
