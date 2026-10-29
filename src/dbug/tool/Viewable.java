package dbug.tool;

import dbug.ui.*;

import static dbug.ui.DebugField.*;

public class Viewable {
		
	protected Class<?> type;
	protected String name;
	protected Object value;

	Viewable(Class<?> type, String name, Object value) {
		this.type = type;
		this.name = name;
		this.value = value;
	}

	public static Viewable entry(Class<?> type, String name, Object value, int depth) {
		return new Viewable(type, name, value);
	}

	public static Viewable entry(Object value) {
		return entry(Object.class, null, value, 1);
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