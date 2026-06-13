package dbug.tool;

import arc.graphics.*;
import arc.struct.*;
import dbug.ui.*;
import dbug.util.*;
import java.lang.*;
import java.lang.reflect.*;

import static dbug.ui.DebugField.*;
import static dbug.ui.MainPanel.*;
import static dbug.util.ParseUtil.*;

public class Writable extends Viewable {
    protected OrderedMap<String, Writable> map = new OrderedMap<>();
    protected Object incoming;
	
	public Writable(Class<?> type, String name, Object value) {
	    super(type, name, value);
		//
		if (isWrapper(type) || type.isPrimitive()) return;
		//
		for (var field : type.getFields()) try {
			if (isWrapper(wrap(field.getType()))) map.put(field.getName(), new Writable(field.getType(), field.getName(), field.get(value)));
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