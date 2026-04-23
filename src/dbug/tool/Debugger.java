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
	//(Temporary) I'm planning on adding one that automatically generates a dummy value 
	public static <T extends Object> T check(T val, T def) {
		//if (val == null) warn();
		return val != null ? val : def;
	}
	
	//add debugger (read-only)
	public static Object dv(String name, Object val) {
		debugger.put(name, new DebugField(name, val.getClass(), viewOnly(val)));
		//
		return val;
	}
	
	//add debugger (writable)
	public static Object dw(String name, Object val) {
	    if (!writable.containsKey(name)) {
	        writable.put(name, new Modifiable(val.getClass(), val));
	        debugger.put(name, writable.get(name).show(name));
	    }
        //
        var v = writable.get(name);
		//
		v.set(val);
		return v.get();
	}
	
	//add a builder function (W.I.P.)
	public static void build(String name) {
		var build = new Builder();
	}
}