package dbug.tool;

import java.lang.reflect.*;

import arc.graphics.*;
import arc.struct.*;
import arc.util.*;

import dbug.ui.*;

import static dbug.ui.DebugField.*;
import static dbug.util.ParseUtil.*;

public class Writable extends Viewable {

	protected OrderedMap<String, Writable> map = new OrderedMap<>();
	protected Object incoming;
	protected @Nullable Field field;
	
	Writable(Class<?> type, String name, Object value) {
		super(type, name, value);
	}

	public static Writable entry(Class<?> type, String name, Object value, int depth, @Nullable Field field) {
        var entry = new Writable(type, name, value);
		//
		entry.field = field;
        entry.init(depth);
		//
        return entry;
    }

	public static Writable entry(Class<?> type, String name, Object value, int depth) {
		var entry = new Writable(type, name, value);
		//
        entry.init(depth);
		//
		return entry;
	}

    private void init(int depth) {
        if (depth < 1 || isWrapper(type) || type.isPrimitive()) return;

        for (var entry : type.getFields()) try {
            if (Modifier.isStatic(entry.getModifiers())) continue;
			// 
            var fieldValue = (value != null) ? entry.get(value) : null;
            // 
            map.put(entry.getName(), Writable.entry(entry.getType(), entry.getName(), fieldValue, depth - 1, entry));
        } catch (Exception e) {/*impossible*/}
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
	
	public Field field() {
		return field;
	}

	public boolean priority() {
		return incoming != null && incoming != value;
	}
}