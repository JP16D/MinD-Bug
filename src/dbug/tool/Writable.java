package dbug.tool;

import arc.graphics.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import dbug.ui.*;
import dbug.util.*;
import mindustry.gen.*;
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
		//
		if (map.size > 0) {
			var table = new DebugField(type, name, group(this, map));
			table.setHighlight(Color.maroon);
			//
			return table;
		} else return new DebugField(type, name, writable(this));
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