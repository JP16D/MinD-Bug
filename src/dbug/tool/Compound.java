package dbug.tool;

import arc.func.*;
import arc.graphics.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static dbug.MDBugVars.*;

public class Compound extends Debuggable{
	//
	private OrderedMap<String, Debuggable> components = new OrderedMap<>();
	
	public Compound(Class<?> type, Prov<?> val) {
		set(type, val);
	}
	
	public void set(Class<?> type, Prov<?> val) {
		this.type = type;
		this.value = val;
		//
		var fields = type.getFields();
		//
		for (var c : fields) {
			var v = new Debuggable(c.getDeclaringClass(), () -> {
				try {
					return c.get(val.get());
				} catch (Exception e) {
					return null;
				}
			});
			//
			components.put(c.getName(), v);
		}
	}
	
	public void prioritize(Compound p) {
		for (var k : components.keys()) {
			var comp = components.get(k);
			//
			if (priority) {
				//
				p.set(comp.type, comp.value);
				priority = false;
			}
		}
	}
	
	public Table actor() {
		return new Table(Tex.pane, t -> {
			for (var k : components.keys()) {
				var v = components.get(k);
				//
				t.add(Debugger.display(Color.darkGray, k, new Table(comp -> {
					comp.field(v.value.get().toString(), Styles.defaultField, (String txt) -> {
						//
						v.parse(v.type, txt);
						//
					}).center().pad(4f);
				})));
				t.row();
			}
			t.button("Set", () -> toggle = true);
		});
	}
}