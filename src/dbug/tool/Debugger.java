package dbug.tool;

import arc.*;
import arc.struct.*;
import dbug.ui.*;

public class Debugger {
	public static final OrderedMap<String, Viewable> entries = new OrderedMap<>();
	
	//returns a default value if main value is null to avoid null error crashes
	//(Temporary) I'm planning on adding one that automatically generates a dummy value 
	public static <T extends Object> T check(T val, T def) {
		//if (val == null) warn();
		return val != null ? val : def;
	}
	
	//add debugger (read-only)
	public static Object dv(String name, Object val) {
		if (!entries.containsKey(name)) entries.put(name, new Viewable(val.getClass(), name, val));
		//
		var entry = entries.get(name);
		entry.set(val);
		//
		return entry.get();
	}
	
	//add debugger (writable)
	public static Object dw(String name, Object val) {
	    if (!entries.containsKey(name)) entries.put(name, new Modifiable(val.getClass(), name, val));
        //
        var entry = (Modifiable) entries.get(name);
        entry.open();
		entry.set(val);
		//
		return entry.get();
	}
	
	//add a builder function (W.I.P.)
	public static void build(String name) {
		var build = new Builder();
	}
}