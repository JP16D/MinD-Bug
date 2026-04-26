package dbug.tool;

import dbug.ui.*;
import java.lang.*;
import java.lang.reflect.*;

import static dbug.ui.DebugField.*;

public class Viewable {
	protected String name;
	protected Object value;
	protected Class<?> type;
	
	public Viewable(String name, Class<?> type, Object value) {
	    this.name = name;
		this.type = type;
		this.value = value;
	}
	
	//table display
	public DebugField show() {
	    return new DebugField(name, type, viewable(this));
	}
	
	public void set(Object value) {
	    this.value = value;
	}
	
	public Object get() {
	    return value;
	}
	
	public Class<?> type() {
	    return type;
	}
}