package dbug.tool;

import java.lang.reflect.*;

import arc.graphics.*;
import arc.struct.*;

import dbug.ui.*;

import static dbug.ui.DebugField.*;
import static dbug.util.ParseUtil.*;

public class Writable extends Viewable {
    protected OrderedMap<String, Writable> map = new OrderedMap<>();
    protected Object incoming;
	
	public Writable(Class<?> type, String name, Object value) {
	    super(type, name, value);
		//
		if (isWrapper(type) || type.isPrimitive()) return;
		//
		for (var field : type.getFields())
		if (isWrapper(wrap(field.getType()))) try {
			if (Modifier.isStatic(field.getModifiers())) continue;

			map.put(field.getName(), new Writable(field.getType(), field.getName(), value != null ? field.get(value) : null));
		} catch (Exception e) {}
	}

	@Override
	public DebugField show() {
	    var table = new DebugField(type, name);
		//
		table.setHighlight(Color.tan);
		table.setContent(map.size > 0 ? group(this, map) : writable(this));
		//
		return table;
	}
	
	public void push(Object input) {
	    incoming = input;
	}
	
	public Object release() {
	    if (priority()) {
	        value = incoming;
	        incoming = null;
	    }
	    return value;
	}
	
	public boolean priority() {
	    return incoming != null && incoming != value;
	}
}