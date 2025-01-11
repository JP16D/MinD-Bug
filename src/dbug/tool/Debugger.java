package dbug.tool;

import arc.*;
import arc.graphics.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import dbug.ui.*;
import mindustry.gen.*;

import static dbug.MDBugVars.*;
import static dbug.ui.DebugField.*;

public class Debugger {
	public static final OrderedMap<String, Modifiable> writable = new OrderedMap<>();
	
	//returns a default value if main value is null to avoid null error crashes
	//temporary, I'm planning on adding one that automatically generates a dummy value 
	public static <T extends Object> T check(T val, T def) {
		//if (val == null) warn();
		return val != null ? val : def;
	}
	
	//add Modifiable object (read-only)
	public static Object dv(String name, Object val) {
		debugger.put(name, new DebugField(name, val.getClass(), viewOnly(val)));
		//
		return val;
	}
	
	//add Modifiable object (writable)
	public static Object dw(String name, Object val) {
		if (!writable.containsKey(name)) writable.put(name, new Modifiable(val.getClass(), val));
		//
		var v = writable.get(name);
		//
		debugger.put(name, v.call(name, val));
		//
		if (v.priority) {
			v.priority = false;
			//
			return v.value;
			//
		} else return val;
	}
	
	//add a builder function
	public static void build(String name) {
		var build = new Builder();
	}
}