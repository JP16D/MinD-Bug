package dbug.tool;

import arc.func.*;
import arc.graphics.*;
import arc.struct.*;

import static MDBugVars.*;

public class Compound {
	private OrderedMap<String, Debuggable> components = new OrderedMap<>();
	
	public Prov<?> build(Class<?> type, String name, Prov<?> val) {
		var fields = type.getFields();
		//
		debugger.put(name, Debugger.display(Color.slate, name, new Table(t -> {
			for (var c : fields) {
				var v = new Debuggable(c.getDeclaringClass(), () -> c.get(val.get()));
				//
				if (components.containsKey(c.getName())) {
					var d = components.get(c.getName());
					//
					if (d.priority) {
						v.set(d.type, d.value);
						//
						d.priority = false;
					}
				}
				//
				components.put(c.getName(), v);
			}
		})));
		//
		return val;
	}
}