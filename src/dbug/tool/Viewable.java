package dbug.tool;

import dbug.ui.*;


import static dbug.ui.DebugField.*;

public class Viewable {
    protected Class<?> type;
	protected String name;
	protected Object value;
	
	public Viewable(Object value) {
	    this.type = Object.class;
	    this.value = value;
	}
	
	public Viewable(Class<?> type, String name, Object value) {
	    this.type = type;
	    this.name = name;
		this.value = value;
	}
	
	public DebugField show() {
	    return new DebugField(type, name, viewable(this));
	}
	
	public Class<?> type() {
	    return type;
	}
	
	public String name() {
	    return name;
	}
	
	public void set(Object value) {
	    this.value = value;
	}
	
	public Object get() {
	    return value;
	}
}