package dbug.tool;

import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;

import static dbug.MDBugVars.*;

public class Compound {
	private OrderedMap<String, Debuggable> components = new OrderedMap<>();
	
	public Prov<?> build(Class<?> type, String name, Prov<?> val) {
		var fields = type.getFields();
		//
		debugger.put(name, Debugger.display(Color.slate, name, new Table(t -> {
			for (var c : fields) {
				var v = new Debuggable(c.getDeclaringClass(), () -> {
					try {
						return c.get(val.get());
					} catch (Exception e) {
						return null;
					}
				});
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